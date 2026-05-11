package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;

import javax.xml.validation.Schema;
import java.util.Map;
import java.util.Optional;

public interface Event extends Comparable<Event> {
    SimEntity getSource();
    long getSerial();
    /// Allows translation between events belonging to the same payload group
    Event cloneAs(Class<? extends Event> asClass) throws IncompatibleEventDataException;
    Object getData();
    void setData(Object obj);
    int getIntraTickOrder();
}
