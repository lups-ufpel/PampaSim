package org.pampasim.memory;

import org.pampasim.core.EventManager;
import org.pampasim.core.Simulation;
import org.pampasim.events.Memory.*;
import org.pampasim.events.Process.Kill;
import org.pampasim.events.Process.Ready;
import org.pampasim.events.Process.Run;
import org.pampasim.events.Process.Schedule;

public class MemoryEventManager extends EventManager {
    public MemoryEventManager(Simulation s) {
        super(s);
    }

    @Override
    public void setupHandlers() {
            this.addEventHandler(Ready.class, simulation);
        this.addEventHandler(Kill.class, simulation);
        this.addEventHandler(Run.class, simulation);
        this.addEventHandler(Schedule.class, simulation);
    }

    @Override
    public void setupTranslations() {
        addTranslation(DeleteTlbEntry.class, FreeProcessMemory.class);
        addTranslation(TranslateVirtualAddress.class, TlbNoTranslation.class);
    }

    @Override
    public void setupFlags() {
        takesTime.put(DiskOperation.class, true);
    }
}
