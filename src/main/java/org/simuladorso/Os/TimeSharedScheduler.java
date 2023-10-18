package org.simuladorso.Os;

import org.simuladorso.Mediator.Mediator;

public abstract class TimeSharedScheduler extends Scheduler{

    public TimeSharedScheduler(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }
    
        
}
