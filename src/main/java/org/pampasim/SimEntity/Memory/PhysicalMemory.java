package org.pampasim.SimEntity.Memory;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimEntity.PampaSimEntity;

public class PhysicalMemory extends PampaSimEntity {
    public PhysicalMemory(Simulation simulation) {
        super(simulation);

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
    }
}
