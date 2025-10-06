package org.pampasim.core.dsl.errors;
import org.pampasim.core.dsl.metadata.Event;

public class UndeclaredEvent extends RuntimeException {
    public UndeclaredEvent(String unkEvent) {
        super("Event " + unkEvent + " used without being previously declared on the events section!");
    }
}
