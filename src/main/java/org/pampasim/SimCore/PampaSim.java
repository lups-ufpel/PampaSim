package org.pampasim.SimCore;

import org.pampasim.SimEntity.*;
import org.pampasim.Utils.PidAllocator;
import java.util.*;

public class PampaSim extends SimulationBase {

    public PampaSim(SimEntity parent) {
        super(parent);
        this.setEventManager(new InterimEventManager(this));
    }

}