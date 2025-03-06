package org.pampasim.SimCore;

import lombok.Getter;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimEntity.SimEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PampaSim implements Simulation {
    private final ArrayList<PampaSimEntity> entityList;
    private final Map<Double, ArrayList<PampaSimEvent>> future; // events that are queued to happen at a specific clock tick (PROCESS_ARRIVAL events)
    private final ArrayList<PampaSimEvent> eventsOnNextClock;
    private final List<org.pampasim.SimCore.PampaSimEvent> finishedProcesses;
    @Getter
    private final EventManager eventManager;
    @Getter
    private double simulationClock;

    public PampaSim() {
        this.entityList = new ArrayList<>();
        this.eventManager = new EventManager();
        this.eventsOnNextClock = new ArrayList<>();
        this.finishedProcesses = new ArrayList<>();
        this.future = new HashMap<>();
        this.simulationClock = 0;
    }

    @Override
    public void addEntity(PampaSimEntity entity) {
        entityList.add(entity);
    }

    @Override
    public void scheduleToNextClock(PampaSimEvent event) {
        eventsOnNextClock.add(event);
    }

    public void scheduleToClock(double clock, PampaSimEvent event) { // used to schedule events before the simulation starts
        if(!future.containsKey(clock)) {
            future.put(clock, new ArrayList<>());
        }
        future.get(clock).add(event);
    }

    public boolean runClockAndProcessEvents() {
        if (future.containsKey(simulationClock)) {
            // checks the list of events that were queued before the simulation started, if there are ones to "arrive"
            // at this clock tick, add them to the list of events to be processed
            ArrayList<PampaSimEvent> queuedEvents = future.get(simulationClock);
            queuedEvents.forEach(this::scheduleToNextClock);
        }

        // Collect all KILL_PROCESS events in finishedProcesses
        List<PampaSimEvent> killProcessEvents = eventsOnNextClock.stream()
                .filter(event -> event.getEventType() == EventType.KILL_PROCESS)
                .toList();

        // Add all KILL_PROCESS events into the finished processes list
        finishedProcesses.addAll(killProcessEvents);

        // Remove KILL_PROCESS events from eventsOnNextClock
        eventsOnNextClock.removeIf(event -> event.getEventType() == EventType.KILL_PROCESS);

        // REFACTOR: changed this function to make use of the event manager. It'll iterate through all future events on queue
        // and send them to the buffer of each entity that handles the event
        executeRunnableEntities();
        if(eventsOnNextClock.isEmpty()) {
            simulationClock += 1;
            return false;
        } else {
            eventsOnNextClock.forEach(eventManager::handleEvent);
            simulationClock += 1;
            return true;
        }
    }

    private void executeRunnableEntities() {
        for (PampaSimEntity pampaSimEntity : entityList) {
            if(pampaSimEntity.getState() == SimEntity.State.RUNNABLE) {
                pampaSimEntity.processEventsinBuffer();
            }
        }
    }

    public <T extends PampaSimEntity> T getEntity(Class<T> entityClass) {
        return entityList.stream()
                .filter(entityClass::isInstance)
                .map(entityClass::cast)
                .findFirst()
                .orElse(null);
    }
}
