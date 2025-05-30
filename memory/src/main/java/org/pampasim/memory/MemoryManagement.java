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
import org.pampasim.memory.entity.VirtualMemory;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.memory.entity.algorithms.RandomAlgorithm;

public class MemoryManagement extends SimulationBase {

    public MemoryManagement(SimulationBase parent) {
        super(parent);
        parent.getEventManager().addEventHandler(Allocate.class, this);
        parent.getEventManager().addEventHandler(Load.class, this);
        parent.getEventManager().addEventHandler(End.class, this);
        parent.getEventManager().addEventHandler(IoOperation.class, this);

        this.setEventManager(new MemoryEventManager(this));

        new VirtualMemory(this, 150);
        new PageTableManager(this);
        new PhysicalMemory(this, 50,
                100, true, new RandomAlgorithm(),
                true, true,
                0.2, 0.01);
    }

    @Override
    public void acceptEvent(Event evt) {
        if ((evt instanceof org.pampasim.events.Process.Ready || // events that are sent back to the parent simulation
                evt instanceof org.pampasim.events.Process.Kill ||
                evt instanceof org.pampasim.events.Process.Schedule ||
                evt instanceof org.pampasim.events.Process.Run)) {
            getParent().acceptEvent(evt);
        } else {
            super.acceptEvent(evt);
        }
    }


}
