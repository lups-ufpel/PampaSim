package org.pampasim.SimCoreRefactor;

import lombok.Getter;
import lombok.Setter;


public class Event {
    @Getter
    private final Process process;
    @Setter
    @Getter
    private EventType eventType;
    private static long serialCounter = 0;
    @Getter
    private final long serial;

    public Event(Process process, EventType eventType) {
        this.process = process;
        this.eventType = eventType;
        this.serial = generateSerial(); // Generates a unique serial number
    }
    private static synchronized long generateSerial() {
        return ++serialCounter;  // Increments and returns a unique serial
    }

}
