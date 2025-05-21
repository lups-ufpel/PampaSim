package org.pampasim.core.dsl.metadata;

import lombok.Getter;

import java.util.Collection;
import java.util.List;

public record Handler(
        Entity owner,
        Event event,
        ChainOp chains,
        String associatedMethod
) {
    public interface ChainOp {
        Collection<Event> getEvents();
    };
    public static class SingleChain implements ChainOp {
        Event event;
        public SingleChain(Event event) { this.event = event; }
        @Override
        public Collection<Event> getEvents() {
            return List.of(this.event);
        }
    };
    @Getter
    public static class ProductChain implements ChainOp {
        List<Event> events;
        public ProductChain(List<Event> events) { this.events = events; }
    };
    @Getter
    public static class SumChain implements ChainOp {
        List<ChainOp> subChains;
        public SumChain(List<ChainOp> subChains) { this.subChains = subChains; }

        @Override
        public Collection<Event> getEvents() {
            return subChains.stream()
                    .flatMap(subC -> subC.getEvents().stream())
                    .toList();
        }
    }
}
