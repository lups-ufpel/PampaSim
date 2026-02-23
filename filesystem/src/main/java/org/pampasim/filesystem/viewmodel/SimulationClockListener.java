package org.pampasim.filesystem.viewmodel;

import org.pampasim.core.SimulationBase;
import javafx.beans.InvalidationListener;

public abstract class SimulationClockListener{

    protected void addSimulationClockListener(SimulationBase simulation) {
        simulation
            .getSimulationClock()
            .addListener((InvalidationListener) obs -> onSimulationTick());
    }

    protected abstract void onSimulationTick();
}
