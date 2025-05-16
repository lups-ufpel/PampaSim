package org.pampasim;

import org.pampasim.core.entity.*;

public class PampaSim extends SimulationBase {

    public PampaSim(SimEntity parent) {
        super(parent);
        this.setEventManager(new InterimEventManager(this));
    }

}