package org.pampasim.core.dsl.metadata;

import java.util.ArrayList;

public record Handler(
        Entity owner,
        EventStatePair eventStatePair,
        ArrayList<AssociatedState> nextStates,
        ArrayList<Event> chainedEvents,
        String associatedMethod
) { }
