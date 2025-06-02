package org.pampasim.memory.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.PageFrameController;
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.memory.ProcessPageTable;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class PhysicalMemory extends AbstractSimEntity {
    private final Logger LOGGER = LogManager.getLogger(PhysicalMemory.class);
    private final PageFrameController mainMemory;
    private final PageFrameController swapFile;
    private final PageReplacementAlgorithm pageReplacementAlgorithm;
    private final Map<Integer, PageTableEntry> frameMap; // map that stores which frames are present in memory
    private final Queue<Event> ioEventQueue;
    private boolean occupied;

    // Policies
    private final boolean globalPageReplacement; // local or global
    private final boolean anticipatedPageLoading; // demand or anticipated
    private final boolean variablePageAllocation; // fixed or variable
    private final double variablePageAllocationTopThreshold;
    private final double variablePageAllocationBottomThreshold;

    // Statistics
    private int pageFaults;
    private int pageHits;

    //TODO: maybe a queue of memory access events will be needed when multiple processor cores exist
    //TODO: add LOGGER debug messages for every event
    //TODO: more error handling

    public PhysicalMemory(Simulation simulation, int mainMemorySize,
                          int swapFileSize, boolean globalReplacementPolicy,
                          PageReplacementAlgorithm pageReplacementAlgorithm,
                          boolean anticipatedPageLoading, boolean variablePageAllocation,
                          double variablePageAllocationTopThreshold, double variablePageAllocationBottomThreshold) {
        super(simulation);

        this.buffer = new PriorityQueue<>(Comparator.comparingInt(this::getEventPriority));
        this.frameMap = new HashMap<>();
        this.mainMemory = new PageFrameController(mainMemorySize);
        this.swapFile = new PageFrameController(swapFileSize);
        this.pageReplacementAlgorithm = pageReplacementAlgorithm;
        this.globalPageReplacement = globalReplacementPolicy;
        this.anticipatedPageLoading = anticipatedPageLoading;
        this.variablePageAllocation = variablePageAllocation;
        this.variablePageAllocationTopThreshold = variablePageAllocationTopThreshold;
        this.variablePageAllocationBottomThreshold = variablePageAllocationBottomThreshold;
        this.ioEventQueue = new LinkedList<>();
        this.occupied = false;
        this.pageFaults = 0;
        this.pageHits = 0;

        // Register event handlers
        simulation.getEventManager().addEventHandler(PageHit.class, this);
        simulation.getEventManager().addEventHandler(PageFault.class, this);
        simulation.getEventManager().addEventHandler(FreeProcessMemory.class, this);
        simulation.getEventManager().addEventHandler(IoOperation.class, this);
        simulation.getEventManager().addEventHandler(DiskOperation.class, this);
    }

    @Override
    public void acceptEvent(Event event) {
        switch (event) {
            case IoOperation e -> acceptIoEventRequest(e); // these events lead into IO operations which can only be done one at a time
            case PageFault e -> acceptIoEventRequest(e);
            default -> super.acceptEvent(event);
        }
    }

    @Override
    protected void managedRun() {
        if (!occupied && !ioEventQueue.isEmpty()) { // no disk operation currently being run
            buffer.add(ioEventQueue.poll()); // add the next event that will lead into an IO operation into the buffer
            occupied = true;
        }
        while (!buffer.isEmpty()) {
            processEvent(buffer.poll());
        }
    }

    @Override
    public boolean shouldRunNextTick() {
        return super.shouldRunNextTick() || (!ioEventQueue.isEmpty() && !occupied);
    }

    public void processEvent(Event event) {
        switch (event) {
            case PageHit e -> handlePageHit(e);
            case PageFault e -> handlePageFault(e);
            case FreeProcessMemory e -> handleFreeProcessMemory(e);
            case IoOperation e -> handleIoOperation(e);
            case DiskOperation e -> handleDiskOperation(e);
            default -> throw new IllegalStateException(
                    "[PhysicalMemory] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void acceptIoEventRequest(Event event) {
        LOGGER.trace("rx {}", event);
        this.ioEventQueue.add(event);
        // the events handled here are non-blocking
    }

    private void handleDiskOperation(DiskOperation event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        LOGGER.debug("Operação de Disco do processo de identificador: {}", process.getPid());
        processMemoryInfo.forwardIoOperation();

        if (processMemoryInfo.getCurrentIoOperationTimeRemaining() <= 0) {
            LOGGER.debug("Termino de Operação de Disco do processo de identificador: {}", process.getPid());
            switch (processMemoryInfo.getCurrentIoOperation()) {
                case PAGE_FAULT -> treatPageFault(process);
                case DISK_ACCESS -> treatDiskAccess(process);
            }
            processMemoryInfo.setCurrentIoOperation(null);
            scheduleToNextClock(new DiskOperationFinished(this, event.getProcess()));
            process.setState(Process.State.WAITING);
            occupied = false;
        } else {
            scheduleToNextClock(new DiskOperation(this, process));
        }
    }

    private void handleIoOperation(IoOperation event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        int currentIoOperationLength = processMemoryInfo.getScheduledIoOperation(process.getCurrExecTime());
        processMemoryInfo.setCurrentIoOperationTimeRemaining(currentIoOperationLength);
        processMemoryInfo.setCurrentIoOperation(ProcessMemoryInfo.IoOperationType.DISK_ACCESS);
        process.setState(Process.State.IO_RUNNING);
        scheduleToNextClock(new DiskOperation(this, event.getProcess()));
    }

    private void handleFreeProcessMemory(FreeProcessMemory event) {
        PidAllocator.Pid processPid = event.getProcess().getPid();
        mainMemory.freeProcessPageFrame(processPid);
        swapFile.freeProcessPageFrame(processPid);
        scheduleToNextClock(new FreeProcessMemoryFinished(this, event.getProcess()));
    }

    private void handlePageFault(PageFault event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        processMemoryInfo.setCurrentIoOperation(ProcessMemoryInfo.IoOperationType.PAGE_FAULT);
        int operationLength = processMemoryInfo.getSwappingOperationsLength();
        processMemoryInfo.setCurrentIoOperationTimeRemaining(operationLength);
        process.setState(Process.State.IO_RUNNING);
        pageFaults++;

        if (variablePageAllocation) {
            handleVariablePageAllocation(process);
        }
        scheduleToNextClock(new DiskOperation(this, process));
    }

    private void handlePageHit(PageHit event) {
        Process process = event.getProcess();
        pageHits++;

        if (variablePageAllocation) {
            handleVariablePageAllocation(process);
        }
        scheduleToNextClock(new ProcessReady(this, event.getProcess()));
    }

    private void handleVariablePageAllocation(Process process) {
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        double pageFaultRate = getPageFaultRate();

        if (pageFaultRate > variablePageAllocationTopThreshold) {
            processMemoryInfo.addMaxFrames();
            LOGGER.trace(
                    "Taxa de page fault foi acima da taxa máxima, adicionando um frame no máximo para o processo de identificador {}, novo máximo: {}",
                    process.getPid(),
                    processMemoryInfo.getMaxFrames()
            );
        } else if (pageFaultRate < variablePageAllocationBottomThreshold) {
            processMemoryInfo.subMaxFrames();
            LOGGER.trace(
                    "Taxa de page fault foi abaixo da taxa mínima, removendo um frame do máximo para o processo de identificador {}, novo máximo: {}",
                    process.getPid(),
                    processMemoryInfo.getMaxFrames()
            );

            if (mainMemory.getTotalProcessPageFrames(process.getPid()) > processMemoryInfo.getMaxFrames()) {
                ArrayList<Integer> accessList = processMemoryInfo.getCurrentAccessList();
                ProcessPageTable processPageTable = processMemoryInfo.getPageTable();
                PageTableEntry[] entries = processPageTable.getEntries(accessList);

                ArrayList<PageTableEntry> validPagePool = getValidPagePool(process, new ArrayList<>(Arrays.asList(entries)));
                PageTableEntry pageToRemove = pageReplacementAlgorithm.pickPagesToSwap(1, validPagePool).getFirst();

                LOGGER.trace(
                        "Realizando swap out de uma página pois o processo de indentificador {} possui mais páginas na memória principal que o seu máximo permite",
                        process.getPid()
                );
                swapOut(pageToRemove);
            }
        }
    }

    private void swapOut(PageTableEntry entry) {
        if (!entry.isValid()) {
            LOGGER.error("Entrada na tabela de páginas não esta na memória principal!");
            return;
        }

        OptionalInt swapOutAddr = swapFile.findFirstContiguousFreeRange(1);
        if (swapOutAddr.isPresent()) {
            mainMemory.freePageFrame(entry.getFrameAddress());
            frameMap.remove(entry.getFrameAddress());
            swapFile.allocatePageFrames(entry.getProcess().getPid(), swapOutAddr.getAsInt(), swapOutAddr.getAsInt());
            entry.setValid(false);
            entry.setFrameAddress(swapOutAddr.getAsInt());

            LOGGER.trace(
                    "Pagina {} do processo de identificador {} swapped out para endereço {} na swapfile",
                    entry.getPageNumber(),
                    entry.getProcess().getPid(),
                    entry.getFrameAddress()
            );
        } else {
            throw new OutOfMemoryError("Não existe espaço na swapfile suficiente para realizar a operação");
        }
    }

    private void swapIn(PageTableEntry entry) {
        if (entry.isValid()) {
            LOGGER.error("Entrada na tabela de páginas já esta na memória principal!");
            return;
        }

        OptionalInt swapInAddr = mainMemory.findFirstContiguousFreeRange(1);
        if (swapInAddr.isPresent()) {
            if (entry.getFrameAddress() != null) {
                swapFile.freePageFrame(entry.getFrameAddress());
            }
            mainMemory.allocatePageFrames(entry.getProcess().getPid(), swapInAddr.getAsInt(), swapInAddr.getAsInt());
            entry.setFrameAddress(swapInAddr.getAsInt());
            frameMap.put(entry.getFrameAddress(), entry);
            entry.setValid(true);

            LOGGER.trace(
                    "Pagina {} do processo de identificador {} swapped in para endereço {} na memória princical",
                    entry.getPageNumber(),
                    entry.getProcess().getPid(),
                    entry.getFrameAddress()
            );
        } else {
            throw new OutOfMemoryError("Não existe espaço na memória principal suficiente para realizar a operação");
        }
    }

    private ArrayList<PageTableEntry> getValidPagePool(Process process, ArrayList<PageTableEntry> excludedPages) {
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        ProcessPageTable processPageTable = processMemoryInfo.getPageTable();
        ArrayList<PageTableEntry> validPagePool;

        if (globalPageReplacement && mainMemory.getTotalProcessPageFrames(process.getPid()) < processMemoryInfo.getMaxFrames()) {
            // Global replacement - can use any page in memory
            validPagePool = frameMap.values().stream()
                    .filter(PageTableEntry::isValid)
                    .filter(entry -> !excludedPages.contains(entry))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            // Local replacement - only use pages from this process
            validPagePool = processPageTable.getValidEntries().stream()
                    .filter(entry -> !excludedPages.contains(entry))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return validPagePool;
    }

    private void treatPageFault(Process process) {
        LOGGER.debug("Processando Page Fault para o processo de identificador {}", process.getPid());
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        ArrayList<Integer> accessList = processMemoryInfo.getCurrentAccessList();
        ProcessPageTable processPageTable = processMemoryInfo.getPageTable();
        PageTableEntry[] entries = processPageTable.getEntries(accessList);

        // Get faulty pages
        ArrayList<PageTableEntry> faultyPages = Arrays.stream(entries)
                .filter(entry -> entry.getFrameAddress() == null || !entry.isValid())
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<PageTableEntry> validPagePool = getValidPagePool(process, faultyPages);

        for (PageTableEntry faultyPage : faultyPages) {
            if (mainMemory.hasFreePageFrames() &&
                    mainMemory.getTotalProcessPageFrames(process.getPid()) < processMemoryInfo.getMaxFrames()) {

                LOGGER.trace(
                        "Ainda há frames na memória principal, realizando swap in da pagina {} do Processo de identificador {}",
                        faultyPage.getPageNumber(),
                        process.getPid()
                );
                swapIn(faultyPage);
            } else {
                PageTableEntry pageToSwap = pageReplacementAlgorithm.pickPagesToSwap(1, validPagePool).getFirst();
                validPagePool.remove(pageToSwap);

                LOGGER.trace(
                        "Não há frames livres na memória principal, realizando swap out da pagina {} do Processo de identificador {}",
                        pageToSwap.getPageNumber(),
                        mainMemory.getPageFrameOwner(pageToSwap.getPageNumber())
                );
                swapOut(pageToSwap);

                LOGGER.trace(
                        "Depois de liberar o frame, realizando swap in da pagina {} do Processo de identificador {}",
                        faultyPage.getPageNumber(),
                        mainMemory.getPageFrameOwner(pageToSwap.getPageNumber())
                );
                swapIn(faultyPage);
            }
        }

        //TODO: Anticipated page loading (Load all pages the process is allowed to have)
        if (anticipatedPageLoading) {
            int freeProcessFrames = processMemoryInfo.getMaxFrames() - mainMemory.getTotalProcessPageFrames(process.getPid());
            int freeMemoryFrames = mainMemory.getTotalPages() - mainMemory.getTotalAllocatedPageFrames();
            int prePagingNumber = min(freeProcessFrames, freeMemoryFrames);

            if (prePagingNumber <= 0) {
                LOGGER.error("Não existe espaço para fazer carregamento de páginas antecipado!");
                return;
            }

            ArrayList<PageTableEntry> pagesToLoad = processMemoryInfo.getWorkingSet();
            if (pagesToLoad.isEmpty()) {
                ArrayList<Integer> currentAccesses = faultyPages.stream()
                        .map(PageTableEntry::getPageNumber)
                        .collect(Collectors.toCollection(ArrayList::new));

                ArrayList<Integer> prefetchList = getPrefetchList(processMemoryInfo, currentAccesses);
                Collections.sort(prefetchList);
                pagesToLoad.addAll(List.of(processMemoryInfo.getPageTable().getEntries(prefetchList)));
                LOGGER.trace("Carregamento antecipado baseado na localidade espacial do processo (Working set ainda não foi calculado)");
            } else {
                LOGGER.trace("Carregamento antecipado baseado no working set do processo");
            }

            int swappedInCounter = 0;
            for (PageTableEntry page : pagesToLoad) {
                if (swappedInCounter < prePagingNumber && !page.isValid()) {
                    LOGGER.trace(
                            "Carregada antecipadamente para a memória principal a página {} do processo de identificador {}",
                            page.getPageNumber(),
                            process.getPid()
                    );
                    swapIn(page);
                    swappedInCounter++;
                }
            }
        }
    }

    private static ArrayList<Integer> getPrefetchList(ProcessMemoryInfo processMemoryInfo, ArrayList<Integer> currentAccesses) {
        int processSize = processMemoryInfo.getSize();
        int prefetchDistance = 2;
        Set<Integer> prefetchSet = new HashSet<>();

        for (int page : currentAccesses) {
            for (int offset = 1; offset <= prefetchDistance; offset++) {
                int nextPage = page + offset;
                if (nextPage < processSize) {
                    prefetchSet.add(nextPage);
                }
            }
        }

        return new ArrayList<>(prefetchSet);
    }

    private void treatDiskAccess(Process process) {
        LOGGER.debug("Processando acesso a disco para o processo de identificador {}", process.getPid());
        // nothing else is required
    }

    private int getEventPriority(Event event) {
        return switch (event) {
            case FreeProcessMemory _e -> 1;   // Highest priority
            default -> Integer.MAX_VALUE;
        };
    }

    public double getPageFaultRate() {
        LOGGER.trace("Current Page Fault Rate: {}%", (((double) pageFaults) / (pageFaults + pageHits))*100);
        return ((double) pageFaults) / (pageFaults + pageHits);
    }
}