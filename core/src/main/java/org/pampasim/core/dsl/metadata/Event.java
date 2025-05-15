package org.pampasim.dsl.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class Event {
    String name;
    public Event(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Event " + getName();
    }
}
