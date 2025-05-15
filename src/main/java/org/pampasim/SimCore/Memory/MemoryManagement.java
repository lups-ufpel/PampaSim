package org.pampasim.SimCore.Memory;

import org.pampasim.SimCore.InterimEventManager;
import org.pampasim.SimCore.SimulationBase;
import org.pampasim.SimEntity.SimEntity;

public class MemoryManagement extends SimulationBase {

    public MemoryManagement(SimEntity parent) {
        super(parent);
        this.setEventManager(new MemoryEventManager(this));
    }
}
