package org.pampasim.SimEntity;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;
import org.pampasim.Utils.GraphVisualizeable;

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
}
