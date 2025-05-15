package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;

public interface Event extends Comparable<Event> {
    SimEntity getSource();
    long getSerial();
    int getCreationTick();
    Event cloneAs(Class<? extends Event> asClass) throws IncompatibleEventDataException;
    Object getData();
}
