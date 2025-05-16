package org.pampasim.memory;

import org.pampasim.SimulationBase;
import org.pampasim.core.entity.SimEntity;

public class MemoryManagement extends SimulationBase {

    public MemoryManagement(SimEntity parent) {
        super(parent);
        this.setEventManager(new MemoryEventManager(this));
    }
}
