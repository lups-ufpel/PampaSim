package org.pampasim;

import org.pampasim.core.EventManager;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;

///  Hardcoded for now
public class InterimEventManager extends EventManager {
    public InterimEventManager(Simulation s) {
        super(s);
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
