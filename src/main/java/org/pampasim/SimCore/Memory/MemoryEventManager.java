package org.pampasim.SimCore.Memory;

import org.pampasim.SimCore.EventManager;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;

public class MemoryEventManager extends EventManager {
    public MemoryEventManager(Simulation s) {
        super(s);
    }

    @Override
    public void setupHandlers() {
    }

    @Override
    public void setupTranslations() {
    }

    @Override
    public void setupFlags() {
        takesTime.put(ProcessRunContinue.class, true);
    }
}
