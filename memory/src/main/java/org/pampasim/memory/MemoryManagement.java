package org.pampasim.memory;

import lombok.Getter;
import org.pampasim.core.SimulationBase;
import org.pampasim.core.events.Event;

import org.pampasim.events.Process.Allocate;
import org.pampasim.events.Process.End;
import org.pampasim.events.Process.IoOperation;
import org.pampasim.events.Process.Load;

import org.pampasim.memory.entity.MMU;
import org.pampasim.memory.entity.PageTableManager;
import org.pampasim.memory.entity.PhysicalMemory;
//import org.pampasim.memory.entity.TLB;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.memory.entity.algorithms.Random;

@Getter
public class MemoryManagement extends SimulationBase {



    public MemoryManagement(SimulationBase parent) {
        super(parent);

        // Register handler
        parent.getEventManager().addEventHandler(Allocate.class, this);
        parent.getEventManager().addEventHandler(Load.class, this);
        parent.getEventManager().addEventHandler(End.class, this);
        parent.getEventManager().addEventHandler(IoOperation.class, this);

        // Use specialized memory event manager
        this.setEventManager(new MemoryEventManager(this));
        /*
        //values defined by user
        int pageSize = 512;
        int maxPagesPerProcess = 1024;
        int framesInRAM = 256;
        int framesInSwap = 1024;
        int swapOperationLength = 3;
        int workingSetWindow = 10;
        String pageSubstitutionAlgorithm = "Random";
        boolean globalPageSubstitution = true;
        boolean anticipatedPageLoading = true;
        int prePagingRange = 5;
        boolean variablePageAllocation = false;
        double topThreshold = 0.75;
        double bottomThreshold = 0.25;
        boolean tlbEnabled = false;
        int tlbEntries = 16;

        // TODO: make sure that all these numbers are powers of 2

        MemoryConfig.initialize(
                pageSize,                   // Page size in KB
                maxPagesPerProcess,         // Max pages per process
                framesInRAM,                // Number of frames in RAM
                framesInSwap,               // Number of frames in swap
                swapOperationLength,        // How long the swapping operations are
                workingSetWindow,           // Working set window size
                pageSubstitutionAlgorithm,  // Algorithm name: "Random", "FIFO", "LRU", etc.
                globalPageSubstitution,     // Global vs Local replacement policy
                anticipatedPageLoading,     // True = Pre-paging, False = Demand paging
                prePagingRange,             // Number of pages to load in advance (if pre-paging is used)
                variablePageAllocation,     // Whether page allocation per process is dynamic
                topThreshold,               // Top fault rate threshold for variable allocation
                bottomThreshold,            // Bottom fault rate threshold for variable allocation
                tlbEnabled,                 // Whether TLB is enabled
                tlbEntries                  // Number of entries in the TLB (if TLB is enabled)
        );

         */

// Initialize memory subsystems
        new MMU(this);
        new PageTableManager(this, 5); // TODO: add a field for the user to define referencedReset;
        new PhysicalMemory(
                this,
                MemoryConfig.isGlobalPageSubstitution(),
                getReplacementAlgorithm(MemoryConfig.getPageSubstitutionAlgorithm()), // helper method
                MemoryConfig.isAnticipatedPageLoading(),
                MemoryConfig.isVariablePageAllocation(),
                MemoryConfig.getVariablePageAllocationTopThreshold(),
                MemoryConfig.getVariablePageAllocationBottomThreshold()
        );
    }
    @Override
    public void acceptEvent(Event evt) {
        // Forward certain events to parent simulation
        if (evt instanceof org.pampasim.events.Process.Ready ||
                evt instanceof org.pampasim.events.Process.Kill ||
                evt instanceof org.pampasim.events.Process.Schedule ||
                evt instanceof org.pampasim.events.Process.Run) {
            getParent().acceptEvent(evt);
        } else {
            super.acceptEvent(evt);
        }
    }

    private PageReplacementAlgorithm getReplacementAlgorithm(String algorithmName) {
        return switch (algorithmName.toUpperCase()) {
            case "RANDOM" -> new Random();
            case "" -> null;
            default -> throw new IllegalArgumentException("Unknown replacement algorithm: " + algorithmName);
        };
    }
}
