package org.pampasim.SimEntity;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.EventType;
import org.pampasim.SimResources.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Scheduler extends PampaSimEntity {
    PriorityQueue<Process> readyList;
    List<Process> terminatedList;
    final long quantum;
    boolean processEnRoute;

    public Scheduler(Simulation simulation) {
        super(simulation);
        readyList = new PriorityQueue<>(Comparator.comparingInt(Process::getPriority));
        terminatedList = new ArrayList<>();
        quantum = 999;
        processEnRoute = false;

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(EventType.SCHEDULE_PROCESS, this);
        simulation.getEventManager().addEventHandler(EventType.RUN_PROCESS_ACK, this);
    }

    @Override
    public void processEventsInBuffer() {
        buffer.forEach(this::processEvent);
        buffer.clear();

        Processor cpu = getSimulation().getEntity(Processor.class);

        if (!readyList.isEmpty() && cpu.isFree() && !processEnRoute) {
            scheduleNextProcess();
        }
    }

    @Override
    public void processEvent(PampaSimEvent event) {
        switch (event.getEventType()) {
            case SCHEDULE_PROCESS -> handleScheduleProcess(event);
            case RUN_PROCESS_ACK -> handleRunProcessAck(event);
            default -> throw new IllegalStateException("[Scheduler] Evento do tipo " + event.getEventType() + " não pode ser tratado, evento serial: " + event.getSerial());
        }
    }

    private void handleScheduleProcess(PampaSimEvent event) {
        readyList.add(event.getProcess());
    }
    private void handleRunProcessAck(PampaSimEvent event) {
        event.getProcess().notifyListenersOnUpdate();
        processEnRoute = false;
    }

    private void scheduleNextProcess() { // TODO: choose between several algorithms, right now it's choosing the one with the highest priority
        if (processEnRoute) { return; }
        Process process = readyList.poll();
        scheduleToNextClock(new PampaSimEvent(process, EventType.DISPATCH_PROCESS));
        processEnRoute = true;
    }

}
