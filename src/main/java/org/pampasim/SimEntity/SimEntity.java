package org.pampasim.SimEntity;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.Utils.GraphVisualizeable;

public interface SimEntity extends GraphVisualizeable {
    enum State {RUNNABLE, WAITING, HOLDING, FINISHED}
    State getState();
    SimEntity setState(State state);
    boolean isStarted();
    boolean start();
    Simulation getSimulation();
    void processEvent(PampaSimEvent evt);
    boolean schedule(PampaSimEvent evt);
}
