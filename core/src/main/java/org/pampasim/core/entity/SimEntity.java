package org.pampasim.core.entity;

import lombok.NonNull;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.core.utils.GraphVisualizeable;

/**
 * Behavior expected from simulation entities.
 * Entities transiently exist in an "unbound" state,
 * where they don't belong to any simulation. All entities
 * not bound to a simulation are in an invalid state, except
 * the parent of all simulations, the root of the hierarchy.
 */
public interface SimEntity extends GraphVisualizeable {
    /**
     * Used to control tick resolution
     * Run -> actively processing events
     * Blocked -> Reached a multi-tick operation
     * Idle -> Won't perform any more work this tick
     */
    enum EntityState { Run, Blocked, Idle };

    /**
     * Associate this entity with another entity, usually a simulation.
     *
     * @param parent the other entity
     * @return success value, the operation can fail.
     */
    boolean bind(@NonNull SimEntity parent);
    EntityState getState();

    /**
     * change entity state from Blocked to Run or Idle.
     * @see EntityState
     */
    void clearBlock();

    Simulation getSimulation();
    Simulation getTopLevelSimulation();
    SimEntity getParent();

    /**
     * process the event queue to perform the operations this entity is responsible for,
     * and dispatch the resulting events.
     */
    void run();
    void scheduleToNextClock(Event evt);
    void acceptEvent(Event evt);

    /**
     * Predicate used by the simulation tick resolver. Take care if overriding!
     * @return whether the entity has work to do and should be run
     */
    boolean shouldRunNextTick();

    /**
     * Use the complex entity state to derive an EntityState value for this entity.
     */
    void updateState();

    /**
     * Eagerly process the whole input event queue until Blocked or Idle.
     * If it ends up Blocked, the input queue might not be empty.
     */
    void eagerRun();
}
