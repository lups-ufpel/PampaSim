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

    public void incrementWaitingTimes() {
        // nothing is needed here, since the IO queue isn't as complex as the scheduler queue, and processes can't be interrupted on their way to performing the operation
    }
}
