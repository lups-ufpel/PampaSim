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
    PageFrameController mainMemory;
    PageFrameController swapFile;
    int maxFramesPerProcess;
    PageReplacementAlgorithm pageReplacementAlgorithm;
    boolean globalPageReplacement;
    // map that stores which frames are present in memory
    Map<Integer, PageTableEntry> frameMap;
    Queue<Event> ioEventQueue;

    //TODO: add LOGGER debug messages for every event

    //TODO: more error handling

    public PhysicalMemory(Simulation simulation, int mainMemorySize, int swapFileSize, int maxFramesPerProcess, boolean globalReplacementPolicy, PageReplacementAlgorithm pageReplacementAlgorithm) {
        super(simulation);

        frameMap = new HashMap<>();
        mainMemory = new PageFrameController(mainMemorySize);
        swapFile = new PageFrameController(swapFileSize);
        this.maxFramesPerProcess = maxFramesPerProcess;
        this.pageReplacementAlgorithm = pageReplacementAlgorithm;
        this.globalPageReplacement = globalReplacementPolicy;

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
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
        if (buffer.stream().noneMatch(element -> element instanceof DiskOperation)) { // no disk operation currently being run
            buffer.add(ioEventQueue.poll()); // add the next event that will lead into an IO operation into the buffer for the current simulation tick
        }
        super.managedRun(); // handle all events
    }

    private void acceptIoEventRequest(Event event) {
        LOGGER.trace("rx {}", event);
        this.ioEventQueue.add(event);
        // the events handled here are non-blocking
    }

    public void processEvent(Event event) {
        switch (event) {
            case PageHit e -> handleMemoryPageHit(e);
            case PageFault e -> handleMemoryPageFault(e);

            case FreeProcessMemory e -> handleMemoryFreeProcessMemory(e);

            case IoOperation e -> handleMemoryIoOperation(e);
            case DiskOperation e -> handleMemoryDiskOperation(e);
            default -> throw new IllegalStateException(
                    "[PhysicalMemory] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryDiskOperation(DiskOperation event) {
        //TODO: Stub
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);

        if (processMemoryInfo.getCurrentIoOperationTimeRemaining() <= 0) {
            scheduleToNextClock(new DiskOperationFinished(this, event.getProcess()));
            //LOGGER.debug("Fim do turno de execução do processo de identificador: {}", process.getPid());
        } else {
            //LOGGER.debug("Continuação da Execução do processo de identificador: {}", process.getPid());
            processMemoryInfo.forwardIoOperation();
            getSimulation().scheduleToNextClock(new org.pampasim.events.Process.Load(this, process));
        }

        scheduleToNextClock(new DiskOperation(this, event.getProcess()));
        scheduleToNextClock(new DiskOperationFinished(this, event.getProcess()));

    }

    private void handleMemoryIoOperation(IoOperation event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        int currentIoOperationLength = processMemoryInfo.getScheduledIoOperation(process.getCurrExecTime());
        processMemoryInfo.setCurrentIoOperationTimeRemaining(currentIoOperationLength);
        processMemoryInfo.setCurrentIoOperationFinished(false);
        scheduleToNextClock(new DiskOperation(this, event.getProcess()));
    }

    private void handleMemoryFreeProcessMemory(FreeProcessMemory event) {
        //TODO: handle this event before all others
        PidAllocator.Pid processPid = event.getProcess().getPid();

        mainMemory.freeProcessPageFrame(processPid);
        swapFile.freeProcessPageFrame(processPid);
        scheduleToNextClock(new FreeProcessMemoryFinished(this, event.getProcess()));
    }

    //TODO: Anticipated page loading (Load all pages the process is allowed to have)

    private void handleMemoryPageFault(PageFault event) {
        Process process = event.getProcess();
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
                if (mainMemory.hasFreePageFrames()) { // means there is a free spot, no need to swap another page out
                    swapIn(process, faultyPage);
                } else { // No free slots, must choose a page to swap out
                    PageTableEntry pageToSwap = pageReplacementAlgorithm.pickPagesToSwap(1, validPagePool).getFirst();

                    swapOut(process, pageToSwap);
                    swapIn(process, faultyPage);
                }
        }

        int operationLength = processMemoryInfo.getSwappingOperationsLength();
        processMemoryInfo.setCurrentIoOperationTimeRemaining(operationLength);
        scheduleToNextClock(new DiskOperation(this, process));
    }

    private void handleMemoryPageHit(PageHit event) {
        //TODO: register main memory accesses
        //Nothing but a hand off is required
        scheduleToNextClock(new ProcessReady(this, event.getProcess()));
    }

    private void swapOut(Process process, PageTableEntry entry) {
        if (!entry.isValid()) {
            LOGGER.error("Page Table Entry is already swapped out!");
            return;
        }
        OptionalInt swapOutAddr = swapFile.findFirstContiguousFreeRange(1);
        if (swapOutAddr.isPresent()) {
            mainMemory.freePageFrame(entry.getFrameAddress());
            frameMap.remove(entry.getFrameAddress());
            swapFile.allocatePageFrames(process.getPid(), swapOutAddr.getAsInt(), swapOutAddr.getAsInt());
            entry.setValid(false);
            entry.setFrameAddress(swapOutAddr.getAsInt());
        } else {
            throw new OutOfMemoryError("Not enough space in the swapfile to perform the swap out operation");
        }
    }

    private void swapIn(Process process, PageTableEntry entry) {
        if (entry.isValid()) {
            LOGGER.error("Page Table Entry is already swapped in!");
        }
        OptionalInt swapInAddr = swapFile.findFirstContiguousFreeRange(1);
        if (swapInAddr.isPresent()) {
            swapFile.freePageFrame(entry.getFrameAddress());
            mainMemory.allocatePageFrames(process.getPid(), swapInAddr.getAsInt(), swapInAddr.getAsInt());
            frameMap.put(entry.getFrameAddress(), entry);
            entry.setValid(true);
            entry.setFrameAddress(swapInAddr.getAsInt());
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
}
