package org.pampasim.memory;

import org.pampasim.core.Simulation;
import org.pampasim.core.SimulationBase;
import org.pampasim.core.events.Event;

import org.pampasim.events.Process.Allocate;
import org.pampasim.events.Process.End;
import org.pampasim.events.Process.IoOperation;
import org.pampasim.events.Process.Load;

import org.pampasim.memory.entity.PageTableManager;
import org.pampasim.memory.entity.PhysicalMemory;
import org.pampasim.memory.entity.TLB;
import org.pampasim.memory.entity.VirtualMemory;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.memory.entity.algorithms.RandomAlgorithm;

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
        new VirtualMemory(this, 150);
        new PageTableManager(this);
        new PhysicalMemory(this,
                50,             // number of frames in main memory
                100,            // number of frames in swapfile
                true,           // global page replacement policy?
                new RandomAlgorithm(), // replacement algorithm
                true,           // anticipated page loading?
                true,           // variable page allocation?
                0.2,            // variable page allocation top page fault threshold
                0.01);          // variable page allocation bottom page fault threshold

        new TLB(this, true, 10);
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
}
