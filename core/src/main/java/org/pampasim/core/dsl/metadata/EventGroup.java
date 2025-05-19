package org.pampasim.core.dsl.metadata;

import java.util.Set;

public record EventGroup(String name, Set<Event> events, Class<?> dataClass) {}
