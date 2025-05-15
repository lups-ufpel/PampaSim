package org.pampasim.dsl.metadata;

import lombok.Getter;

@Getter
public class EventStatePair {
    private Event event;
    private AssociatedState state;

    public EventStatePair(Event event, AssociatedState state) {
        this.event = event;
        this.state = state;
    }

    @Override
    public String toString() {
        return "(" + getEvent() + "," + getState() + ")";
    }
}
