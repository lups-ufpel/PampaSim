package org.simuladorso.Os;

import org.simuladorso.Mediator.Mediator;

public abstract class SpaceSharedScheduler extends Scheduler{

    public SpaceSharedScheduler(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }
}
