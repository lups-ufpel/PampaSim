package org.pampasim.dsl.metadata;

import lombok.Getter;
import org.pampasim.SimCoreRefactor.EventType;

@Getter
public class EventStatePair {
    private String eventName;
    private AssociatedState state;

    public EventStatePair(String eventName, AssociatedState state) {
        this.eventName = eventName;
        this.state = state;
    }

    @Override
    public String toString() {
        return "(" + getEventName() + "," + getState() + ")";
    }
}
