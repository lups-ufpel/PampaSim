package org.pampasim.core.dsl.metadata;

import java.util.ArrayList;

public record Handler(
        Entity owner,
        Event event,
        ArrayList<Event> chainedEvents,
        String associatedMethod
) { }
