package org.pampasim.SimEntity;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;
import org.pampasim.Utils.GraphVisualizeable;

public interface SimEntity extends GraphVisualizeable {
    enum State {RUNNABLE, WAITING, HOLDING, FINISHED}
    State getState();
    SimEntity setState(State state);
    boolean isStarted();
    boolean start();
    Simulation getSimulation();
    void processEvent(Event evt);
    void run();
    void scheduleToNextClock(Event evt);
    void acceptEvent(Event evt);
    void logInfo(String info);
}
