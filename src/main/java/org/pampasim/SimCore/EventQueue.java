package org.pampasim.SimCore;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

public interface EventQueue {
    void addEvent(PampaSimEvent event);
    Stream<PampaSimEvent> stream();
    int size();
    boolean isEmpty();
    PampaSimEvent first() throws NoSuchElementException;
}
