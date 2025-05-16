package org.pampasim.core.dsl.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class Event {
    String name;
    Class<?> dataClass;
    boolean realtime;
    public Event(String name, Class<?> dataClass, boolean realtime) {
        this.name = name;
        this.dataClass = dataClass;
        this.realtime = realtime;
    }
    @Override
    public String toString() {
        return "Event " + getName() + ((getDataClass() != null)? " transmitting " + getDataClass() : "");
    }
}
