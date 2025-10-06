package org.pampasim.core.dsl.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class Event {
    final String name;
    final String groupName;
    final boolean realtime;
    public Event(String name, EventGroup group, boolean realtime) {
        this.name = name;
        this.groupName = group.name();
        this.realtime = realtime;
    }
    @Override
    public String toString() {
        return "Event " + getName() + " of group " + groupName;
    }
}
