package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;

public interface Event extends Comparable<Event> {
    SimEntity getSource();
    long getSerial();
    int getCreationTick();
    Event cloneAs(Class<? extends Event> asClass) throws IncompatibleEventDataException;
    Object getData();
}
