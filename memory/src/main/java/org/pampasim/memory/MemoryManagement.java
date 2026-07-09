package org.pampasim.memory;

import lombok.Getter;
import lombok.NonNull;
import org.pampasim.core.SimulationBase;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.Event;

import org.pampasim.events.Process.Allocate;
import org.pampasim.events.Process.End;
import org.pampasim.events.Process.IoOperation;
import org.pampasim.events.Process.Load;

import org.pampasim.memory.entity.MMU;
import org.pampasim.memory.entity.PageTableManager;
import org.pampasim.memory.entity.PhysicalMemory;
//import org.pampasim.memory.entity.TLB;
import org.pampasim.memory.entity.algorithms.*;
import org.pampasim.resources.ModuleSimulation;
import org.pampasim.resources.ModuleSimulationBase;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.MemoryProcessCreationData;
import org.pampasim.resources.memory.ProcessMemoryInfo;

import java.util.HashMap;
import java.util.IdentityHashMap;

@Getter
public class MemoryManagement extends ModuleSimulationBase {

    @Getter private static final IdentityHashMap<Process, ProcessMemoryInfo> processMemoryInfos = new IdentityHashMap<>();
    @Getter private static final HashMap<Process.CreationData, MemoryProcessCreationData> pendingMemoryInfoBindings = new HashMap<>();

    public MemoryManagement() {
        super();
        // Use specialized memory event manager
        this.setEventManager(new MemoryEventManager(this));

        // Initialize memory subsystems
        var mmu = new MMU();
        mmu.bind(this);
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
    public boolean bind(@NonNull SimEntity parent) {
        var ok = super.bind(parent);
        if (ok) {
            var simulation = parent.getSimulation();
            // Register handler
            simulation.getEventManager().addEventHandler(Allocate.class, this);
            simulation.getEventManager().addEventHandler(Load.class, this);
            simulation.getEventManager().addEventHandler(End.class, this);
            simulation.getEventManager().addEventHandler(IoOperation.class, this);
        }
        return ok;
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
            case "FIFO" -> new FIFO();
            case "FIFOCLOCK" -> new FIFOClock();
            case "LFU" -> new LFU();
            case "LRU" -> new LRU();
            case "NRU" -> new NRU();
            case "" -> null;
            default -> throw new IllegalArgumentException("Unknown replacement algorithm: " + algorithmName);
        };
    }

    public void incrementWaitingTimes() {
        // nothing is needed here, since the IO queue isn't as complex as the scheduler queue, and processes can't be interrupted on their way to performing the operation
    }

    @Override
    public void applyConfig(Object config) throws ModuleSimulation.ConfigError {
        if (! (config instanceof org.pampasim.resources.memory.MMU xmlConf)) {
            throw new ConfigError("type mismatch for config object");
        }
        MemoryConfig.initialize(
                MemoryConfig.getPageSize(),
                MemoryConfig.getMaxPagesPerProcess(),
                MemoryConfig.getFramesInRAM(),
                MemoryConfig.getFramesInSwap(),

                (int)xmlConf.getSwapOperationLength(),
                (int)xmlConf.getWorkingSetWindow(),
                xmlConf.getPageSubstitutionAlgorithm(),
                xmlConf.isGlobalPageSubstitution(),
                xmlConf.isAnticipatedPageLoading(),
                (int)xmlConf.getPrePagingRange(),
                xmlConf.isVariablePageAllocation(),
                xmlConf.getVariablePageAllocationThresholds().getFirst(),
                xmlConf.getVariablePageAllocationThresholds().getLast(),
                xmlConf.isTlbEnabled(),
                (int)xmlConf.getTlbEntries()
        );
        // no need to invalidate
    }

    @Override
    public Class<?> configClass() {
        return org.pampasim.resources.memory.MMU.class;
    }
}
