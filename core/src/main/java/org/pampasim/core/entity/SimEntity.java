package org.pampasim.core.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.core.utils.GraphVisualizeable;

public interface SimEntity extends GraphVisualizeable {
    enum EntityState { Run, Blocked, Idle };
    EntityState getState();
    void clearBlock();
    boolean isStarted();
    boolean start();
    Simulation getSimulation();
    Simulation getTopLevelSimulation();
    SimEntity getParent();
    //void processEvent(Event evt); this is internal, acceptEvent is the public interface
    void run();
    void scheduleToNextClock(Event evt);
    void acceptEvent(Event evt);
    void logInfo(String info);
    boolean shouldRunNextTick();
    void updateState();
    public void runUntilBlockedorIdle();
}
