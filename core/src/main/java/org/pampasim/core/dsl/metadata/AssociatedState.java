package org.pampasim.core.dsl.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
@EqualsAndHashCode
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
