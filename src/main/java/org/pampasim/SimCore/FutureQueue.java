package org.pampasim.SimCore;

import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.pampasim.SimCore.events.Event;

public class FutureQueue implements EventQueue {

    private final SortedSet<Event> sortedSet = new TreeSet<>();
    private long maxEventsNumber;
    @Override
    public void addEvent(Event event) {
        sortedSet.add(event);
        System.out.println("Evento adicionado, total de eventos: " + sortedSet.size());
        maxEventsNumber = Math.max(maxEventsNumber, sortedSet.size());
    }

    @Override
    public Stream<Event> stream() {
        return sortedSet.stream();
    }

    @Override
    public int size() {
        return sortedSet.size();
    }

    @Override
    public boolean isEmpty() {
        return sortedSet.isEmpty();
    }

    @Override
    public Event first() throws NoSuchElementException {
        return sortedSet.first();
    }
    public boolean remove(final Event event) {
        return sortedSet.remove(event);
    }

    public void clear() {
        sortedSet.clear();
    }
}
