package org.pampasim.core.entity;

import lombok.NonNull;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.core.utils.GraphVisualizeable;

public interface SimEntity extends GraphVisualizeable {
    enum EntityState { Run, Blocked, Idle };
    boolean bind(@NonNull SimEntity parent);
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
    boolean shouldRunNextTick();
    void updateState();
    public void runUntilBlockedorIdle();
}
