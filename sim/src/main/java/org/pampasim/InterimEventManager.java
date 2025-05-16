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
        if (event instanceof org.pampasim.events.Process.Kill) {
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
        addTranslation(org.pampasim.events.Process.Allocate.class, org.pampasim.events.Process.Ready.class);
        addTranslation(org.pampasim.events.Process.End.class, org.pampasim.events.Process.Kill.class);
        addTranslation(org.pampasim.events.Process.Dispatch.class, org.pampasim.events.Process.Run.class);
        addTranslation(org.pampasim.events.Process.IoOperation.class, org.pampasim.events.Process.Schedule.class);
    }

    @Override
    public void setupFlags() {
        takesTime.put(org.pampasim.events.Process.RunContinue.class, true);
    }
}
