package org.pampasim.SimEntityImpls;

import org.pampasim.SimCore.EventType;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Scheduler extends EntityImpl {
    PriorityQueue<Process> readyList;
    List<Process> terminatedList;
    final long quantum;

    public enum State {
        // codegen states
    }
    private State state;

    public Scheduler(Simulation simulation) {
        super(simulation);
        readyList = new PriorityQueue<>(Comparator.comparingInt(Process::getPriority));
        terminatedList = new ArrayList<>();
        quantum = 999;

        // codegen register handlers
    }

    @Override
    public void run() {
        buffer.forEach(this::processEvent);
        buffer.clear();

        Processor cpu = getSimulation().getEntity(Processor.class);

        if (!readyList.isEmpty() && cpu.isFree() ) {//&& !processEnRoute) {
            scheduleNextProcess();
        }
    }

    // codegen processEvent

    private void handleScheduleProcess(PampaSimEvent event) {
        readyList.add(event.getProcess());
    }
    private void handleRunProcessAck(PampaSimEvent event) {
        event.getProcess().notifyListenersOnUpdate();
        //processEnRoute = false;
    }

    private void scheduleNextProcess() { // TODO: choose between several algorithms, right now it's choosing the one with the highest priority
        Process process = readyList.poll();
        scheduleToNextClock(new PampaSimEvent(process, EventType.DISPATCH_PROCESS));
    }

}
