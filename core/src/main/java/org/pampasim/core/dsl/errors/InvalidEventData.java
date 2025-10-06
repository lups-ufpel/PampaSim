package org.pampasim.core.dsl.errors;
import org.pampasim.core.dsl.metadata.Event;

public class InvalidEventData extends RuntimeException {
    public InvalidEventData(String dataClassName) {
        super("Event transmitting " + dataClassName + " are invalid, data class couldn't be found!");
    }
}
