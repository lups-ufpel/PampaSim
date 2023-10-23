package org.simuladorso.Os;

import org.simuladorso.Mediator.Mediator;

import java.util.List;

public abstract class SpaceSharedScheduler extends Scheduler {

    public SpaceSharedScheduler(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }
    protected abstract Process dequeue(List<Process> processQueue);
}
