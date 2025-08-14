package org.pampasim.memory.entity;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.events.ProcessEvent;
import org.pampasim.memory.MemoryConfig;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.FrameController;
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.memory.ProcessPageTable;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class PhysicalMemory extends AbstractSimEntity {
    private final Logger LOGGER = LogManager.getLogger(PhysicalMemory.class);
    @Getter
    private final FrameController mainMemory;
    @Getter
    private final FrameController swapFile;
    private final PageReplacementAlgorithm pageReplacementAlgorithm;
    private final Map<Integer, PageTableEntry> frameMap; // map that stores which frames are present in memory
    @Getter
    private final Queue<Event> ioEventQueue;
    private boolean occupied;

    // Policies
    private final boolean globalPageReplacement; // local or global
    private final boolean anticipatedPageLoading; // demand or anticipated
    private final boolean variablePageAllocation; // fixed or variable
    private final double variablePageAllocationTopThreshold;
    private final double variablePageAllocationBottomThreshold;

    // Statistics
    @Getter
    private int pageFaults;
    @Getter
    private int pageHits;
    @Getter
    private PageTableEntry lastSwappedOutPage = null;
    @Getter
    private ArrayList<PageTableEntry> lastSwappedInPages = new ArrayList<>();

    public PhysicalMemory(Simulation simulation, boolean globalReplacementPolicy,
                          PageReplacementAlgorithm pageReplacementAlgorithm,
                          boolean anticipatedPageLoading, boolean variablePageAllocation,
                          double variablePageAllocationTopThreshold, double variablePageAllocationBottomThreshold) {
        super(simulation);

        this.buffer = new PriorityQueue<>(Comparator.comparingInt(this::getEventPriority));
        this.frameMap = new HashMap<>();
        this.mainMemory = new FrameController(MemoryConfig.getFramesInRAM());
        this.swapFile = new FrameController(MemoryConfig.getFramesInSwap());
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

        simulation.getEventManager().addEventHandler(PageHit.class, this);
        simulation.getEventManager().addEventHandler(PageFault.class, this);
        simulation.getEventManager().addEventHandler(FreeProcessMemory.class, this);
        simulation.getEventManager().addEventHandler(IoOperation.class, this);
        simulation.getEventManager().addEventHandler(DiskOperation.class, this);
    }

    @Override
    public void acceptEvent(Event event) {
        switch (event) {
            case IoOperation e -> acceptIoEventRequest(e);
            case PageFault e -> acceptIoEventRequest(e);
            default -> super.acceptEvent(event);
        }
    }

    @Override
    protected void managedRun() {
        if (!occupied && !ioEventQueue.isEmpty()) {
            buffer.add(ioEventQueue.poll());
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
    }

    private void handleDiskOperation(DiskOperation event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);

        LOGGER.debug("Operação de Disco do processo de identificador: {}", process.getPid());
        processMemoryInfo.forwardIoOperation();
        incrementIoWaitingTimes();

        if (processMemoryInfo.getCurrentIoOperationTimeRemaining() <= 0) {
            LOGGER.debug("Termino de Operação de Disco do processo de identificador: {}", process.getPid());
            switch (processMemoryInfo.getCurrentIoOperation()) {
                case PAGE_FAULT -> treatPageFault(process);
                case DISK_ACCESS -> treatDiskAccess(process);
            }
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
        Process process = event.getProcess();
        mainMemory.freeProcessFrames(process);
        swapFile.freeProcessFrames(process);
        scheduleToNextClock(new FreeProcessMemoryFinished(this, process));
    }

    private void handlePageFault(PageFault event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        processMemoryInfo.setCurrentIoOperation(ProcessMemoryInfo.IoOperationType.PAGE_FAULT);
        int operationLength = processMemoryInfo.getMemoryConfigData().getSwappingOperationsLength();
        processMemoryInfo.setCurrentIoOperationTimeRemaining(operationLength);
        process.setState(Process.State.IO_RUNNING);
        pageFaults++;
        processMemoryInfo.registerPageFault();

        if (variablePageAllocation) {
            handleVariablePageAllocation(process);
        }
        scheduleToNextClock(new DiskOperation(this, process));
    }

    private void handlePageHit(PageHit event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        pageHits++;
        processMemoryInfo.registerPageHit();
        Integer currentAccess = processMemoryInfo.getCurrentAccess();
        int pageNo = MemoryConfig.extractPageNumber(currentAccess);
        PageTableEntry entry = processMemoryInfo.getPageTable().getEntry(pageNo);
        pageReplacementAlgorithm.registerReference(entry);

        if (variablePageAllocation) {
            handleVariablePageAllocation(process);
        }
        scheduleToNextClock(new ProcessReady(this, event.getProcess()));
    }

    private void handleVariablePageAllocation(Process process) {
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        double pageFaultRate = processMemoryInfo.getPageFaultRate();

        if (pageFaultRate > variablePageAllocationTopThreshold) {
            processMemoryInfo.addMaxFrames();
            LOGGER.trace(
                    "Taxa de page fault do processo foi acima da taxa máxima, adicionando um frame no máximo para o processo de identificador {}, novo máximo: {}",
                    process.getPid(),
                    processMemoryInfo.getMaxFrames()
            );
        } else if (pageFaultRate < variablePageAllocationBottomThreshold) {
            processMemoryInfo.subMaxFrames();
            LOGGER.trace(
                    "Taxa de page fault do processo foi abaixo da taxa mínima, removendo um frame do máximo para o processo de identificador {}, novo máximo: {}",
                    process.getPid(),
                    processMemoryInfo.getMaxFrames()
            );

            if (mainMemory.getTotalProcessFrames(process) > processMemoryInfo.getMaxFrames()) {
                Integer access = processMemoryInfo.getCurrentAccess();
                int pageNumber = MemoryConfig.extractPageNumber(access);
                ProcessPageTable processPageTable = processMemoryInfo.getPageTable();
                PageTableEntry entry = processPageTable.getEntry(pageNumber);

                ArrayList<PageTableEntry> validPagePool = getValidPagePool(process, List.of(entry));
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

        entry.setDirty(false);
        pageReplacementAlgorithm.registerSwapOut(entry);
        this.lastSwappedOutPage = entry;

        if (entry.isFileBacked()) {
            mainMemory.freeFrame(entry.getFrameNumber());
            frameMap.remove(entry.getFrameNumber());
            entry.setValid(false);
            entry.setFrameNumber(null, MemoryConfig.getPageSize());
            LOGGER.trace(
                    "Pagina {} do processo de identificador {} swapped out para o sistema de arquivos (file-backed)",
                    entry.getPageNumber(),
                    entry.getProcess().getPid()
            );
            return;
        }

        OptionalInt swapOutAddr = swapFile.findFirstContiguousFreeRange(1);
        if (swapOutAddr.isPresent()) {
            mainMemory.freeFrame(entry.getFrameNumber());
            frameMap.remove(entry.getFrameNumber());
            swapFile.allocateFrames(entry, swapOutAddr.getAsInt());
            entry.setValid(false);
            entry.setFrameNumber(swapOutAddr.getAsInt(), MemoryConfig.getPageSize());

            LOGGER.trace(
                    "Pagina {} do processo de identificador {} swapped out para endereço {} na swapfile",
                    entry.getPageNumber(),
                    entry.getProcess().getPid(),
                    entry.getFrameNumber()
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
            if (entry.getFrameNumber() != null) {
                swapFile.freeFrame(entry.getFrameNumber());
            }
            mainMemory.allocateFrames(entry, swapInAddr.getAsInt());
            entry.setFrameNumber(swapInAddr.getAsInt(), MemoryConfig.getPageSize());
            frameMap.put(entry.getFrameNumber(), entry);
            entry.setValid(true);

            this.lastSwappedInPages.add(entry);

            LOGGER.trace(
                    "Pagina {} do processo de identificador {} swapped in para endereço {} na memória princical",
                    entry.getPageNumber(),
                    entry.getProcess().getPid(),
                    entry.getFrameNumber()
            );
            pageReplacementAlgorithm.registerSwapIn(entry);
        } else {
            throw new OutOfMemoryError("Não existe espaço na memória principal suficiente para realizar a operação");
        }
    }

    private ArrayList<PageTableEntry> getValidPagePool(Process process, List<PageTableEntry> excludedPages) {
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        ProcessPageTable processPageTable = processMemoryInfo.getPageTable();
        ArrayList<PageTableEntry> validPagePool;

        if (globalPageReplacement && mainMemory.getTotalProcessFrames(process) < processMemoryInfo.getMaxFrames()) {
            validPagePool = frameMap.values().stream()
                    .filter(PageTableEntry::isValid)
                    .filter(entry -> !excludedPages.contains(entry))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            validPagePool = processPageTable.getValidEntries().stream()
                    .filter(entry -> !excludedPages.contains(entry))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return validPagePool;
    }

    private void treatPageFault(Process process) {
        LOGGER.debug("Processando Page Fault para o processo de identificador {}", process.getPid());
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        Integer access = processMemoryInfo.getCurrentAccess();
        int pageNumber = MemoryConfig.extractPageNumber(access);
        ProcessPageTable processPageTable = processMemoryInfo.getPageTable();
        PageTableEntry entry = processPageTable.getEntry(pageNumber);

        this.lastSwappedInPages.clear();

        if (entry.getFrameNumber() == null || !entry.isValid()) {
            ArrayList<PageTableEntry> validPagePool = getValidPagePool(process, List.of(entry));

            if (mainMemory.hasFreeFrames() &&
                    mainMemory.getTotalProcessFrames(process) < processMemoryInfo.getMaxFrames()) {

                LOGGER.trace(
                        "Ainda há frames na memória principal, realizando swap in da pagina {} do Processo de identificador {}",
                        entry.getPageNumber(),
                        process.getPid()
                );
                swapIn(entry);
            } else {
                PageTableEntry pageToSwap = pageReplacementAlgorithm.pickPagesToSwap(1, validPagePool).getFirst();
                validPagePool.remove(pageToSwap);

                LOGGER.trace(
                        "Não há frames livres na memória principal, realizando swap out da pagina {} do Processo de identificador {}",
                        pageToSwap.getPageNumber(),
                        mainMemory.getFrameOwner(pageToSwap.getPageNumber())
                );
                swapOut(pageToSwap);

                LOGGER.trace(
                        "Depois de liberar o frame, realizando swap in da pagina {} do Processo de identificador {}",
                        entry.getPageNumber(),
                        mainMemory.getFrameOwner(pageToSwap.getPageNumber())
                );
                swapIn(entry);
            }
        }

        if (anticipatedPageLoading) {
            int freeProcessFrames = processMemoryInfo.getMaxFrames() - mainMemory.getTotalProcessFrames(process);
            int freeMemoryFrames = mainMemory.getTotalFrames() - mainMemory.getTotalAllocatedFrames();
            int prePagingNumber = min(freeProcessFrames, freeMemoryFrames);

            if (prePagingNumber <= 0) {
                LOGGER.trace("Não existe espaço para fazer carregamento de páginas antecipado!");
                return;
            }

            ArrayList<Integer> prefetchList = getPrefetchList(processMemoryInfo, List.of(pageNumber));
            Collections.sort(prefetchList);
            ArrayList<PageTableEntry> pagesToLoad = processMemoryInfo.getPageTable().getEntries(prefetchList);
            LOGGER.trace("Carregamento antecipado baseado na localidade espacial do processo");

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

    private static ArrayList<Integer> getPrefetchList(ProcessMemoryInfo processMemoryInfo, List<Integer> currentAccesses) {
        int processSize = processMemoryInfo.getSize();
        int prefetchDistance = MemoryConfig.getPrePagingRange();
        Set<Integer> prefetchSet = new HashSet<>();

        for (int access : currentAccesses) {
            for (int offset = 1; offset <= prefetchDistance; offset++) {
                int nextPage = access + offset;
                if (nextPage < processSize) {
                    prefetchSet.add(nextPage);
                }
            }
        }

        return new ArrayList<>(prefetchSet);
    }

    private void treatDiskAccess(Process process) {
        LOGGER.debug("Processando acesso a disco para o processo de identificador {}", process.getPid());
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

    public void incrementIoWaitingTimes() {
        ioEventQueue.forEach(ioEvent -> {((ProcessEvent) ioEvent).getProcess().getModuleInfo(ProcessMemoryInfo.class).incrementIoWaitingTime();});
    }
}