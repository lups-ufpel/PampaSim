package org.pampasim.SimEntity.Schedulers;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.EventType;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimEntity.Processor;
import org.pampasim.SimResources.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public abstract class Scheduler extends PampaSimEntity {
    boolean processEnRoute;

    public Scheduler(Simulation simulation) {
        super(simulation);
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

        if (cpu.isFree() && !processEnRoute) {
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

    protected abstract void handleScheduleProcess(PampaSimEvent event);
    protected void handleRunProcessAck(PampaSimEvent event) {
        event.getProcess().notifyListenersOnUpdate();
        processEnRoute = false;
    }

    protected abstract void scheduleNextProcess();
}
