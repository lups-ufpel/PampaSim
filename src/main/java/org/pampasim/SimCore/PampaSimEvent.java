package org.pampasim.SimCore;
import org.pampasim.SimResources.Process;

import lombok.Getter;


@Getter
public class PampaSimEvent {
    private final Process process;
    private final EventType eventType;
    private static long serialCounter = 0;
    private final long serial;

    public PampaSimEvent(Process process, EventType eventType) {
        this.process = process;
        this.eventType = eventType;
        this.serial = generateSerial(); // Generates a unique serial number
    }
    public PampaSimEvent changeType(EventType type) { // the events aren't renamed when transitioning between types, a new one is created
        return new PampaSimEvent(process, type);
    }
    private static synchronized long generateSerial() {
        return ++serialCounter;  // Increments and returns a unique serial
    }

}
