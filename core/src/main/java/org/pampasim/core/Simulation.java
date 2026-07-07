package org.pampasim.core;

import javafx.beans.value.ObservableIntegerValue;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;

/**
 * Simulations are entities composed of other entities governed
 * by an independent simulation clock.
 * Simulations are usually associated with a Module.
 * Every simulation needs an EventManager implementation to route events
 * between the contained entities.
 *
 * `hasPendingEvents` is used to manage when to execute the simulation entity,
 * so make sure its implementation is prim and proper.
 * @see org.pampasim.core.entity.Module
 * @see org.pampasim.core.entity.AbstractSimEntity
 */
public interface Simulation extends SimEntity {
    void addEntity(SimEntity entity);
    void scheduleToClock(int clock, Event event);
    <T extends SimEntity> T getEntity(Class<T> entityClass);
    EventManager getEventManager();
    RealClock getRealClock();
    PidAllocator getPidAllocator();
    ObservableIntegerValue getSimulationClock();

    /**
     * returns whether the simulation has any pending / scheduled events
     */
    boolean hasPendingEvents();
}
