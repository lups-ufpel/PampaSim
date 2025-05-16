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
        if (event instanceof ProcessEvent.Kill) {
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
        addTranslation(ProcessEvent.Allocate.class, ProcessEvent.Ready.class);
        addTranslation(ProcessEvent.End.class, ProcessEvent.Kill.class);
        addTranslation(ProcessEvent.Dispatch.class, ProcessEvent.Run.class);
        addTranslation(ProcessEvent.IoOperation.class, ProcessEvent.Schedule.class);
    }

    @Override
    public void setupFlags() {
        takesTime.put(ProcessEvent.RunContinue.class, true);
    }
}
