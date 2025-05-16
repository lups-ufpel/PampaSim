package org.pampasim.core.dsl.errors;
import org.pampasim.core.dsl.metadata.Event;

public class DuplicateEvent extends RuntimeException {
    public DuplicateEvent(Event a) {
        super("Event " + a + " was defined twice!");
    }
}
