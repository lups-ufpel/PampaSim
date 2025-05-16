package org.pampasim.core.dsl.errors;
import org.pampasim.core.dsl.metadata.Event;

public class UndeclaredEvent extends RuntimeException {
    public UndeclaredEvent(Event event) {
        super("Event " + event.getName() + " used without being previously declared on the events section!");
    }
}
