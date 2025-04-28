package org.pampasim.SimCore;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import lombok.Getter;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimEntity.Processor;
import org.pampasim.SimEntity.Scheduler;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.Utils.GraphVisualizeable;
import org.pampasim.Utils.PidAllocator;
import org.pampasim.dsl.spec.Spec;

import static guru.nidi.graphviz.model.Factory.*;

import java.util.*;

public class PampaSim implements Simulation {
    protected final ArrayList<PampaSimEntity> entityList;
    private Map<Integer, ArrayList<PampaSimEvent>> eventsSchedule; // events that are queued to happen at a specific clock tick (PROCESS_ARRIVAL events)
    private final TreeSet<Integer> futureKeys; // used in the check if there are any "future" events after any given clock
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
        this.finishedProcesses = new ArrayList<>();
        this.eventsSchedule = new HashMap<>();
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
        scheduleToClock(simulationClock+1, event);
    }

    public void scheduleToClock(int clock, final PampaSimEvent event) { // used to schedule events before the simulation starts
        if(!eventsSchedule.containsKey(clock)) {
            eventsSchedule.put(clock, new ArrayList<>());
            futureKeys.add(clock);
        }
        eventsSchedule.get(clock).add(event);
    }

    public boolean runClockAndProcessEvents() {
        ArrayList<PampaSimEvent> currentEvents;

        if (eventsSchedule.containsKey(simulationClock)) {
            // checks the list of events that were queued before the simulation started, if there are ones to "arrive"
            // at this clock tick, add them to the list of events to be processed
             currentEvents = new ArrayList<>(eventsSchedule.get(simulationClock));
        } else {
            currentEvents = new ArrayList<>();
        }
        executeRunnableEntities();

        List<PampaSimEvent> killProcessEvents = currentEvents.stream()
                .filter(event -> event.getEventType() == EventType.KILL_PROCESS)
                .peek(event -> System.out.println("[PampaSim] Processo com Pid " + event.getProcess().getPid() +
                        " finalizou sua execução e foi terminado com sucesso"))
                .toList(); // collects and logs KILL_PROCESS events

        finishedProcesses.addAll(killProcessEvents); // adds to the finished processes list

        // REFACTOR: changed this function to make use of the event manager. It'll iterate through all future events on queue
        // and send them to the buffer of each entity that handles the event
        if(currentEvents.isEmpty() && futureKeys.higher(simulationClock) == null) { // no events on next clock and also no future events scheduled
            simulationClock += 1;
            return false;
        } else {
            // Necessary to create a copy of the eventsOnNextClock to iterate over since handleEvent can add a KILL_PROCESS event
            // to the list as it's being iterated over
            currentEvents.stream()
                    .filter(event -> event.getEventType() != EventType.KILL_PROCESS)
                    .forEach(eventManager::handleEvent); // processes all events except KILL_PROCESS events
            simulationClock += 1;
            System.out.println("clock:" + simulationClock);
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

    public boolean isFresh() {
        return this.eventsSchedule.isEmpty() && (getSimulationClock() == 0);
    }

    public void applySpec(Spec s) {
        // only apply specs to a clean sim
        if (!isFresh()) {
            throw new RuntimeException("Can't apply spec to already running simulation!");
        }
        // Here's where we'd instantiate the right scheduler
        this.pidAllocator = s.getPidAlloc();
        this.eventsSchedule = s.getEventSchedule();
    }


}