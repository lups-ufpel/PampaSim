package org.pampasim.core.dsl.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class Event {
    String name;
    Class<?> dataClass;
    public Event(String name, Class<?> dataClass) {
        this.name = name;
        this.dataClass = dataClass;
    }
    @Override
    public String toString() {
        return "Event " + getName() + ((getDataClass() != null)? " transmitting " + getDataClass() : "");
    }
}
