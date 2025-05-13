package org.pampasim.SimCore;

import org.pampasim.SimCore.events.*;

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
        System.out.println("here, got called!");
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
