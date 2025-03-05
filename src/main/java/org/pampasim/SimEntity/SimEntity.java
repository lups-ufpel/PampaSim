package org.pampasim.SimEntity;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;

public interface SimEntity {
    enum State {RUNNABLE, WAITING, HOLDING, FINISHED}
    State getState();
    SimEntity setState(State state);
    boolean isStarted();
    boolean start();
    Simulation getSimulation();
    void processEvent(PampaSimEvent evt);
    void processEventsInBuffer();
    void scheduleToNextClock(PampaSimEvent evt);
    void acceptEvent(PampaSimEvent evt);
    void processEventsinBuffer();

}
