package org.pampasim.SimEntityImpls;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimEntity.PampaSimEntity;

public abstract class EntityImpl extends PampaSimEntity {

    public EntityImpl(Simulation simulation) {
        super(simulation);
    }
    ///  Used to facilitate working with the yet-to-be State enums
    Enum<?> getState(String stateName) { assert(false); return null; }
    boolean stateMatches(String stateName) { assert(false); return false; }
    void setState(String stateName) { assert(false); return; }
}
