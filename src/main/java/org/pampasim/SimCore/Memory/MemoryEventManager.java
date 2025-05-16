package org.pampasim.SimCore.Memory;

import org.pampasim.SimCore.EventManager;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.Memory.*;

public class MemoryEventManager extends EventManager {
    public MemoryEventManager(Simulation s) {
        super(s);
    }

    @Override
    public void setupHandlers() {
    }

    @Override
    public void setupTranslations() {
        addTranslation(MemoryDeleteTlbEntry.class, MemoryFreeProcessMemory.class);
        addTranslation(MemoryTranslateVirtualAddress.class, MemoryTlbNoTranslation.class);
    }

    @Override
    public void setupFlags() {
        takesTime.put(MemoryTimeAdvance.class, true);
    }
}
