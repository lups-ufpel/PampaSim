package org.pampasim.dsl.metadata;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.stream.Collectors;

/// Each entity instance will have their own state
/// Enum class, which needs to be referenced here somehow.
/// In a perfect world, that association would be encoded in the
/// type system, but Java don't roll like that. So for now I'll
/// try making a member be a reference to the Class instance of
/// the appropriate Enum.
/// [StackOverflow re: generic over Enums](https://stackoverflow.com/a/24466815)
@Setter
@Getter
public class Entity implements Mergeable<Entity> {
    protected String name = null;
    protected ArrayList<Handler> handlers = new ArrayList<>();

    public ArrayList<String> allStateNames() {
        return handlers.stream()
                .map(h -> h.eventStatePair().getState().getStateName())
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<String> allAcceptedEventNames() {
        return handlers.stream()
                .map(h -> h.eventStatePair().getEventName())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Entity merge(Entity other) {
        if (this.name != null && other.name != null && !this.name.equals(other.name)) {
            System.err.println("name change while merging entities, this was "
                    + this.getName() + " new one is " + other.getName());
        }
        this.name = other.name;
        this.handlers.addAll(other.handlers);
        return this;
    }
}
