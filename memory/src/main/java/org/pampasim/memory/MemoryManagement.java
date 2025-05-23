package org.pampasim.memory;

import org.pampasim.core.SimulationBase;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.events.Memory.*;
import org.pampasim.events.Process.Kill;
import org.pampasim.events.Process.Load;
import org.pampasim.events.Process.Schedule;

public class MemoryManagement extends SimulationBase {

    public MemoryManagement(SimEntity parent) {
        super(parent);
        this.setEventManager(new MemoryEventManager(this));
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
