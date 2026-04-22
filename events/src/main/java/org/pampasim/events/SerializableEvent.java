package org.pampasim.events;

/// events with payloads that are all serializable
public interface SerializableEvent extends org.pampasim.core.events.Event {
    // TODO/FIXME: these are horribly unsafe,
    // I couldn't find a way to use the type system properly
    Object serialize();
    Object deserialize(Object o);
}