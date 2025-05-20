package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.PageFrameController;
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.memory.ProcessPageTable;

import java.util.*;
import java.util.stream.Collectors;

public class PhysicalMemory extends AbstractSimEntity {

    PageFrameController mainMemory;
    PageFrameController swapFile;
    int maxFramesPerProcess;
    PageReplacementAlgorithm pageReplacementAlgorithm;
    // map that stores which frames are present in memory
    Map<Long, PageTableEntry> frameMap;



    public PhysicalMemory(Simulation simulation, int mainMemorySize, int swapFileSize, int maxFramesPerProcess, boolean globalReplacementPolicy, PageReplacementAlgorithm pageReplacementAlgorithm) {
        super(simulation);

        frameMap = new HashMap<>();
        mainMemory = new PageFrameController(mainMemorySize);
        swapFile = new PageFrameController(swapFileSize);
        this.maxFramesPerProcess = maxFramesPerProcess;
        this.pageReplacementAlgorithm = pageReplacementAlgorithm;

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
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
        //TODO: can result in a MemoryTimeAdvance or a MemoryIoOperationFinished
        //TODO: Stub
        scheduleToNextClock(new DiskOperation(this, event.getProcess()));
        scheduleToNextClock(new DiskOperationFinished(this, event.getProcess()));

    }

    private void handleMemoryIoOperation(IoOperation event) {
        //TODO: Stub
        scheduleToNextClock(new DiskOperation(this, event.getProcess()));
    }

    private void handleMemoryFreeProcessMemory(FreeProcessMemory event) {
        //TODO: Stub
        scheduleToNextClock(new FreeProcessMemoryFinished(this, event.getProcess()));
    }

    private void handleMemoryPageFault(PageFault event) {
        //TODO: Stub
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);

        ArrayList<Integer> accessList = processMemoryInfo.getCurrentAccessList();
        ProcessPageTable processPageTable = processMemoryInfo.getPageTable();
        PageTableEntry[] entries = processPageTable.getEntries(accessList);
        ArrayList<PageTableEntry> faultyPages = Arrays.stream(entries)
                .filter(entry -> entry.getFrameAddress() == null || !entry.isValid())
                .collect(Collectors.toCollection(ArrayList::new));

        Set<Integer> framesInMainMemory = mainMemory.getProcessPageFrame(process.getPid());
        Set<Integer> framesInSwapFile = swapFile.getProcessPageFrame(process.getPid());

        // TODO: handle anticipated vs demand paging policy, right now it's purely by demand

        for (PageTableEntry faultyPage : faultyPages) {
            if (mainMemory.getTotalProcessPageFrames(process.getPid()) < maxFramesPerProcess) { // if the process can still allocate more pages for itself
                OptionalInt freePage = mainMemory.findFirstContiguousFreeRange(1);
                if (freePage.isPresent()) { // means there is a free spot, no need to swap another page out
                    mainMemory.allocatePageFrames(process.getPid(),freePage.getAsInt(), freePage.getAsInt());
                    if (faultyPage.getFrameAddress() != null) { // means it exists in the swapfile
                        swapFile.freePageFrame(faultyPage.getFrameAddress()); // remove it from the swapfile after bringing it to the main memory
                    }
                    faultyPage.setFrameAddress(freePage.getAsInt()); // set the page table entry to the new allocated address
                    faultyPage.setValid(true); // the page is now in the main memory
                } else { // No free slots, must choose a page to swap out
                    //TODO: swapping logic using PageReplacementAlgorithm
                }
            } else {
                // Process reached the max number of pages its allowed to have in the main memory
                // TODO: swapping logic using PageReplacementAlgorithm, though with only the process' pages as candidates, even if the policy is global
            }
        }



        scheduleToNextClock(new DiskOperation(this, process));
    }

    private void handleMemoryPageHit(PageHit event) {
        //TODO: Stub
        scheduleToNextClock(new ProcessReady(this, event.getProcess()));
    }
}
