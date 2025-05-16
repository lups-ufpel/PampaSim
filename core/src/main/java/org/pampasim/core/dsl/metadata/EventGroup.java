package org.pampasim.core.dsl.metadata;

import java.util.Set;

public record EventGroup(String prefix, Set<Event> events, Class<?> dataClass) {}
