package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimCore.events.Event;
import org.pampasim.Utils.PidAllocator;
import org.pampasim.dsl.spec.Spec;

public interface Simulation {
    void addEntity(PampaSimEntity entity);
    void scheduleToNextClock(final Event event);
    void scheduleToClock(int clock, Event event);
    boolean runClockAndProcessEvents();
    <T extends PampaSimEntity> T getEntity(Class<T> entityClass);
    EventManager getEventManager();
    PidAllocator getPidAllocator();
    int getSimulationClock();

    /// returns whether the simulation is "fresh" as in loading a Spec won't override any configurations
    boolean isFresh();
    /// makes this simulation inherit the configurations defined in a Spec
    /// throws a runtime error if not "fresh"
    void applySpec(Spec s);
}
