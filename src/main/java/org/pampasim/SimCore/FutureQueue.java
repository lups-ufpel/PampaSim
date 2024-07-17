package org.pampasim.SimCore;

import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public class FutureQueue implements EventQueue {

    private final SortedSet<PampaSimEvent> sortedSet = new TreeSet<>();
    private long maxEventsNumber;
    @Override
    public void addEvent(PampaSimEvent event) {
        sortedSet.add(event);
        System.out.println("Evento adicionado, total de eventos: " + sortedSet.size());
        maxEventsNumber = Math.max(maxEventsNumber, sortedSet.size());
    }

    @Override
    public Stream<PampaSimEvent> stream() {
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
    public PampaSimEvent first() throws NoSuchElementException {
        return sortedSet.first();
    }
    public boolean remove(final PampaSimEvent event) {
        return sortedSet.remove(event);
    }
}