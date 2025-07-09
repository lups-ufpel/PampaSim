package org.pampasim;

import org.pampasim.core.EventManager;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.Event;
import org.pampasim.events.*;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.resources.Process;

///  Hardcoded for now
public class InterimEventManager extends EventManager {
    public InterimEventManager(Simulation s) {
        super(s);
    }

    @Override
    public void setupHandlers() {
        addEventHandler(org.pampasim.events.Process.Kill.class, this.simulation); // ignore Kills
    }

    @Override
    public void setupTranslations() {
        addTranslation(org.pampasim.events.Process.Allocate.class, org.pampasim.events.Process.Ready.class);
        addTranslation(org.pampasim.events.Process.End.class, org.pampasim.events.Process.Kill.class);
        addTranslation(org.pampasim.events.Process.Load.class, org.pampasim.events.Process.Run.class);
        addTranslation(org.pampasim.events.Process.IoOperation.class, org.pampasim.events.Process.Schedule.class);
    }

    @Override
    public void setupFlags() {
        takesTime.put(org.pampasim.events.Process.Run.class, true);
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof org.pampasim.events.Process.Dispatch dispatch) {
            if (simulation.getEntity(MemoryManagement.class) == null) {
                dispatch.getProcess().setState(Process.State.RUNNING);
            }
        }
    }
}
