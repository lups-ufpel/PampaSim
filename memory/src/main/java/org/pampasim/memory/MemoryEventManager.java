package org.pampasim.memory;

import org.pampasim.core.EventManager;
import org.pampasim.core.Simulation;

public class MemoryEventManager extends EventManager {
    public MemoryEventManager(Simulation s) {
        super(s);
    }

    @Override
    public void setupHandlers() {
    }

    @Override
    public void setupTranslations() {
        //addTranslation(MemoryDeleteTlbEntry.class, MemoryFreeProcessMemory.class);
        //addTranslation(MemoryTranslateVirtualAddress.class, MemoryTlbNoTranslation.class);
    }

    @Override
    public void setupFlags() {
        //takesTime.put(MemoryTimeAdvance.class, true);
    }
}
