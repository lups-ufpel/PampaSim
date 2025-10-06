package org.pampasim.core.dsl.metadata;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class Entity {
    protected String name = null;
    protected Map<Event, Handler> handlers = new HashMap<>();
    protected List<Event> transmitList = new ArrayList<>();

    public Set<Event> allAcceptedEvents() {
        return new HashSet<>(handlers
                .keySet());
    }

    public boolean isTranslatable() {
        return transmitList.isEmpty()
            && handlers.values().stream()
            // the only conditional (and therefore untranslatable) chain operation
                .noneMatch(h -> h.chains() instanceof Handler.SumChain)
            // check if the all the handlers are translatable
            && handlers.entrySet().stream()
                .allMatch(e ->
                    e.getValue().chains().getEvents()
                            .stream()
                            .allMatch(outEvent -> Objects.equals(e.getKey().groupName, outEvent.groupName))
                    );
    }

    @Override
    public String toString() {
        return "Entity " + getName();
    }
}