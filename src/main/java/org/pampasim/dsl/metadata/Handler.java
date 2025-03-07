package org.pampasim.dsl.metadata;

import lombok.Getter;

import java.util.ArrayList;

public record Handler(
        Entity owner,
        EventStatePair eventStatePair,
        ArrayList<AssociatedState> nextStates,
        ArrayList<Event> chainedEvents,
        String associatedMethod
) { }
