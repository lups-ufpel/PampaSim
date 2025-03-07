package org.pampasim.dsl.metadata;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class AssociatedState {
    private Entity owner;
    private String stateName;

    public AssociatedState(Entity owner, String stateName) {
        this.owner = owner;
        this.stateName = stateName;
    }

    @Override
    public String toString() {
        return "(" + getStateName() + " of " + getOwner() + ")";
    }
}
