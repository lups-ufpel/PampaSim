package org.pampasim.SimCore;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.pampasim.SimCore.events.Event;

public interface EventQueue {
    void addEvent(Event event);
    Stream<Event> stream();
    int size();
    boolean isEmpty();
    Event first() throws NoSuchElementException;
}
