package org.simuladorso.Os;

//import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.simuladorso.Os.Process;
import org.simuladorso.Mediator.Mediator;

public class SchedulerFCFS extends SpaceSharedScheduler {

    
    public SchedulerFCFS(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }

    @Override
    protected Process dequeue(List<Process> processQueue){
        return processQueue.remove(0);
    }
}
