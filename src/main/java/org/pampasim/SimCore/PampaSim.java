package org.pampasim.SimCore;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import lombok.Getter;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.Utils.GraphVisualizeable;
import org.pampasim.Utils.PidAllocator;

import static guru.nidi.graphviz.model.Factory.*;

import java.util.*;

public class PampaSim implements Simulation {
    protected final ArrayList<PampaSimEntity> entityList;
    private final Map<Integer, ArrayList<PampaSimEvent>> future; // events that are queued to happen at a specific clock tick (PROCESS_ARRIVAL events)
    private final TreeSet<Integer> futureKeys; // used in the check if there are any "future" events after any given clock
    private final ArrayList<PampaSimEvent> eventsOnNextClock;
    private final List<PampaSimEvent> finishedProcesses;
    @Getter
    private final EventManager eventManager;
    @Getter
    private int simulationClock;
    @Getter
    private PidAllocator pidAllocator;

    public PampaSim() {
        this.entityList = new ArrayList<>();
        this.eventManager = new EventManager(this);
        this.eventsOnNextClock = new ArrayList<>();
        this.finishedProcesses = new ArrayList<>();
        this.future = new HashMap<>();
        this.futureKeys = new TreeSet<>();
        this.simulationClock = 0;
        this.pidAllocator = new PidAllocator();
    }

    @Override
    public void addEntity(PampaSimEntity entity) {
        entityList.add(entity);
    }

    @Override
    public void scheduleToNextClock(final PampaSimEvent event) {
        eventsOnNextClock.add(event);
    }

    public void scheduleToClock(int clock, final PampaSimEvent event) { // used to schedule events before the simulation starts
        if(!future.containsKey(clock)) {
            future.put(clock, new ArrayList<>());
            futureKeys.add(clock);
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

        executeRunnableEntities();

        // Collect all KILL_PROCESS events in finishedProcesses
        List<PampaSimEvent> killProcessEvents = eventsOnNextClock.stream()
                .filter(event -> event.getEventType() == EventType.KILL_PROCESS)
                .toList();

        for (PampaSimEvent event : killProcessEvents) {
            System.out.println("[PampaSim] Processo com Pid " + event.getProcess().getPid() + " finalizou sua execução e foi terminado com sucesso" );
        }

        // Add all KILL_PROCESS events into the finished processes list
        finishedProcesses.addAll(killProcessEvents);

        // Remove KILL_PROCESS events from eventsOnNextClock
        eventsOnNextClock.removeIf(event -> event.getEventType() == EventType.KILL_PROCESS);

        // REFACTOR: changed this function to make use of the event manager. It'll iterate through all future events on queue
        // and send them to the buffer of each entity that handles the event
        if(eventsOnNextClock.isEmpty() && futureKeys.higher(simulationClock) == null) { // no events on next clock and also no future events scheduled
            simulationClock += 1;
            return false;
        } else {
            // Necessary to create a copy of the eventsOnNextClock to iterate over since handleEvent can add a KILL_PROCESS event
            // to the list as it's being iterated over
            List<PampaSimEvent> eventsToProcess = new ArrayList<>(eventsOnNextClock);
            eventsOnNextClock.clear();
            eventsToProcess.forEach(eventManager::handleEvent);
            simulationClock += 1;
            return true;
        }
    }

    private void executeRunnableEntities() {
        for (PampaSimEntity pampaSimEntity : entityList) {
            if(pampaSimEntity.getState() == SimEntity.State.RUNNABLE) {
                pampaSimEntity.processEventsInBuffer();
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