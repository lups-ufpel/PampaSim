package org.pampasim.core;

import javafx.beans.value.ObservableIntegerValue;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;

public interface Simulation extends SimEntity {
    void addEntity(SimEntity entity);
    void scheduleToClock(int clock, Event event);
    <T extends SimEntity> T getEntity(Class<T> entityClass);
    EventManager getEventManager();
    RealClock getRealClock();
    PidAllocator getPidAllocator();
    ObservableIntegerValue getSimulationClock();

    /// returns whether the simulation has any pending / scheduled events
    boolean hasPendingEvents();
}
