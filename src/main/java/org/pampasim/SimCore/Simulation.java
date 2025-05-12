package org.pampasim.SimCore;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimCore.events.Event;
import org.pampasim.Utils.PidAllocator;
import org.pampasim.dsl.spec.Spec;

public interface Simulation extends SimEntity {
    void addEntity(SimEntity entity);
    void scheduleToClock(int clock, Event event);
    <T extends SimEntity> T getEntity(Class<T> entityClass);
    EventManager getEventManager();
    PidAllocator getPidAllocator();
    int getSimulationClock();

    /// returns whether the simulation is "fresh" as in loading a Spec won't override any configurations
    boolean isFresh();
    /// returns whether the simulation has any pending / scheduled events
    boolean hasPendingEvents();
    /// makes this simulation inherit the configurations defined in a Spec
    /// throws a runtime error if not "fresh"
    void applySpec(Spec s);
}
