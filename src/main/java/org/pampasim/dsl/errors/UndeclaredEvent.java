package org.pampasim.dsl.errors;
import org.pampasim.dsl.metadata.Event;

public class UndeclaredEvent extends RuntimeException {
    public UndeclaredEvent(Event event) {
        super("Event " + event.name() + " used without being previously declared on the events section!");
    }
}
