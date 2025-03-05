package org.pampasim.dsl.metadata;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
public class Entity {
    protected String name = null;
    protected Map<String, Handler> handlers = new HashMap<>();

    public List<String> allStateNames() {
        return handlers.values().stream()
                .map(h -> h.eventStatePair().getState().getStateName())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> allAcceptedEventNames() {
        return new ArrayList<>(handlers.keySet());
    }
}