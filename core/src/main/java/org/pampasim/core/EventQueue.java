package org.pampasim.core;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.pampasim.core.events.Event;

public interface EventQueue {
    void addEvent(Event event);
    Stream<Event> stream();
    int size();
    boolean isEmpty();
    Event first() throws NoSuchElementException;
}
