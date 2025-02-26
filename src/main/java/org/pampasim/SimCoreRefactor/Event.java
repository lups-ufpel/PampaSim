package org.pampasim.SimCoreRefactor;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Event {
    private final Process process;
    @Setter
    private EventType eventType;

    public Event(Process process, EventType eventType) {
        this.process = process;
        this.eventType = eventType;
    }

}
