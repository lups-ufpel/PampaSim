package org.pampasim;

import org.pampasim.core.EventManager;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.Event;
import org.pampasim.events.*;

///  Hardcoded for now
public class InterimEventManager extends EventManager {
    public InterimEventManager(Simulation s) {
        super(s);
    }

    @Override
    protected Event translateEvent(Event event) {
        if (event instanceof ProcessKill) {
            ((SimulationBase)simulation).blackHoleEvent(event);
            return null;
        }
        return super.translateEvent(event);
    }

    @Override
    public void setupHandlers() {
    }

    @Override
    public void setupTranslations() {
        addTranslation(ProcessAllocate.class, ProcessReady.class);
        addTranslation(ProcessEnd.class, ProcessKill.class);
        addTranslation(ProcessDispatch.class, ProcessRun.class);
        addTranslation(ProcessIoOperation.class, ProcessSchedule.class);
    }

    @Override
    public void setupFlags() {
        takesTime.put(ProcessRunContinue.class, true);
    }
}
