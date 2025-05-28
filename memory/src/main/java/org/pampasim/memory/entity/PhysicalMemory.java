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

public class PhysicalMemory extends AbstractSimEntity {

    private final Logger LOGGER = LogManager.getLogger(PhysicalMemory.class);
    private final PageFrameController mainMemory;
    private final PageFrameController swapFile;
    private final int maxFramesPerProcess;
    private final PageReplacementAlgorithm pageReplacementAlgorithm;
    // map that stores which frames are present in memory
    private final Map<Integer, PageTableEntry> frameMap;
    private final Queue<Event> ioEventQueue;
    private boolean occupied;

    //TODO: add LOGGER debug messages for every event

    //TODO: more error handling

    // policies
    private final boolean globalPageReplacement; // local or global
    private final boolean anticipatedPageLoading; // demand or anticipated
    private final boolean variablePageAllocation; // fixed or variable

    // TODO: implement anticipated page loading
    // TODO: implement variable page loading using PFF (Page Fault Frequency) algorithm, which is an extension of the working set concept.

    private int pageFaults;
    private int pageHits;

    public PhysicalMemory(Simulation simulation, int mainMemorySize,
                          int swapFileSize, int maxFramesPerProcess,
                          boolean globalReplacementPolicy, PageReplacementAlgorithm pageReplacementAlgorithm,
                          boolean anticipatedPageLoading, boolean variablePageAllocation) {
        super(simulation);

        frameMap = new HashMap<>();
        mainMemory = new PageFrameController(mainMemorySize);
        swapFile = new PageFrameController(swapFileSize);
        this.maxFramesPerProcess = maxFramesPerProcess;
        this.pageReplacementAlgorithm = pageReplacementAlgorithm;
        this.globalPageReplacement = globalReplacementPolicy;
        this.anticipatedPageLoading = anticipatedPageLoading;
        this.variablePageAllocation = variablePageAllocation;
        ioEventQueue = new LinkedList<>();
        occupied = false;
        pageFaults = 0;
        pageHits = 0;

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
        if ((!occupied) && (!ioEventQueue.isEmpty())) { // no disk operation currently being run
            buffer.add(ioEventQueue.poll()); // add the next event that will lead into an IO operation into the buffer for the current simulation tick
            occupied = true;
        }
        while (!buffer.isEmpty()) {
            processEvent(buffer.poll());
        }
    }

    private void acceptIoEventRequest(Event event) {
        LOGGER.trace("rx {}", event);
        this.ioEventQueue.add(event);
        // the events handled here are non-blocking
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
        //TODO: handle this event before all others
        PidAllocator.Pid processPid = event.getProcess().getPid();

        mainMemory.freeProcessPageFrame(processPid);
        swapFile.freeProcessPageFrame(processPid);
        scheduleToNextClock(new FreeProcessMemoryFinished(this, event.getProcess()));
    }

    //TODO: Anticipated page loading (Load all pages the process is allowed to have)

    private void handlePageFault(PageFault event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        processMemoryInfo.setCurrentIoOperation(ProcessMemoryInfo.IoOperationType.PAGE_FAULT);
        int operationLength = processMemoryInfo.getSwappingOperationsLength();
        processMemoryInfo.setCurrentIoOperationTimeRemaining(operationLength);
        process.setState(Process.State.IO_RUNNING);

        pageFaults++;
        scheduleToNextClock(new DiskOperation(this, process));
    }

    private void handlePageHit(PageHit event) {
        //TODO: register main memory accesses
        //Nothing but a hand off is required
        pageHits++;
        scheduleToNextClock(new ProcessReady(this, event.getProcess()));
    }

    private void swapOut(PageTableEntry entry) {
        if (!entry.isValid()) {
            LOGGER.error("Page Table Entry is already swapped out!");
            return;
        }
        OptionalInt swapOutAddr = swapFile.findFirstContiguousFreeRange(1);
        if (swapOutAddr.isPresent()) {
            mainMemory.freePageFrame(entry.getFrameAddress());
            frameMap.remove(entry.getFrameAddress());
            swapFile.allocatePageFrames(entry.getProcess().getPid(), swapOutAddr.getAsInt(), swapOutAddr.getAsInt());
            entry.setValid(false);
            entry.setFrameAddress(swapOutAddr.getAsInt());
            LOGGER.trace("Pagina {} do processo de identificador {} swapped out para endereço {} na swapfile",entry.getPageNumber(), entry.getProcess().getPid(), entry.getFrameAddress());
        } else {
            throw new OutOfMemoryError("Not enough space in the swapfile to perform the swap out operation");
        }
    }

    private void swapIn(PageTableEntry entry) {
        if (entry.isValid()) {
            LOGGER.error("Page Table Entry is already swapped in!");
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
            LOGGER.trace("Pagina {} do processo de identificador {} swapped in para endereço {} na memória princical",entry.getPageNumber(), entry.getProcess().getPid(), entry.getFrameAddress());

        } else {
            throw new OutOfMemoryError("Not enough space in the main memory to perform the swap in operation");
        }
    }

    private ArrayList<PageTableEntry> getValidPagePool(Process process, ArrayList<PageTableEntry> faultyPages) {
        ProcessPageTable processPageTable = process.getModuleInfo(ProcessMemoryInfo.class).getPageTable();

        // Get valid page pool (global or local)
        ArrayList<PageTableEntry> validPagePool;

        if (globalPageReplacement && mainMemory.getTotalProcessPageFrames(process.getPid()) < maxFramesPerProcess) { // if the process can still allocate more pages for itself
            validPagePool = frameMap.values().stream()
                    .filter(PageTableEntry::isValid)
                    .filter(entry -> !faultyPages.contains(entry))  // Exclude faulty pages
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            // Process reached the max number of pages it's allowed to have in the main memory
            validPagePool = processPageTable.getValidEntries().stream()
                    .filter(entry -> !faultyPages.contains(entry))  // Exclude faulty pages
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return validPagePool;
    }
    @Override
    public boolean shouldRunNextTick() {
        return super.shouldRunNextTick() || ((!ioEventQueue.isEmpty()) && (!occupied));
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
            if (mainMemory.hasFreePageFrames() && mainMemory.getTotalProcessPageFrames(process.getPid()) < processMemoryInfo.getSize()) { // means there is a free spot, no need to swap another page out
                LOGGER.trace("Ainda há frames na memória principal, realizando swap in da pagina {} do Processo de identificador {}",faultyPage.getPageNumber(), process.getPid());
                swapIn(faultyPage);
            } else { // No free slots, must choose a page to swap out
                PageTableEntry pageToSwap = pageReplacementAlgorithm.pickPagesToSwap(1, validPagePool).getFirst();
                validPagePool.remove(pageToSwap); // remove the page that was picked so it isn't picked twice

                LOGGER.trace("Não há frames livres na memória principal, realizando swap out da pagina {} do Processo de identificador {}",pageToSwap.getPageNumber(), mainMemory.getPageFrameOwner(pageToSwap.getPageNumber()));
                swapOut(pageToSwap);
                LOGGER.trace("Depois de liberar o frame, realizando swap in da pagina {} do Processo de identificador {}",faultyPage.getPageNumber(), mainMemory.getPageFrameOwner(pageToSwap.getPageNumber()));
                swapIn(faultyPage);
            }
        }
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
}
