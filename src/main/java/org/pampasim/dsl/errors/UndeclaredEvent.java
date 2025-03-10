package org.pampasim.dsl.errors;
import org.pampasim.dsl.metadata.Event;

public class UndeclaredEvent extends RuntimeException {
    public UndeclaredEvent(Event event) {
        super("Event " + event.getName() + " used without being previously declared on the events section!");
    }
}
