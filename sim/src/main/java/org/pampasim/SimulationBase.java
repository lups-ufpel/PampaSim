package org.pampasim;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Compass;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import lombok.Getter;
import org.pampasim.core.EventManager;
import org.pampasim.core.EventSchedule;
import org.pampasim.core.RealClock;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.Event;
import org.pampasim.events.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.entity.ProcessManager;
import org.pampasim.entity.Processor;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.ProcessorCore;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.dsl.spec.Spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public abstract class SimulationBase extends AbstractSimEntity implements Simulation {
    protected final ArrayList<SimEntity> entityList;
    private EventSchedule eventsSchedule;
    private final List<Event> lastClockInputs = new ArrayList<>();
    private final List<Event> lastClockOutputs = new ArrayList<>();
    @Getter
    protected EventManager eventManager;
    @Getter
    private int simulationClock;
    @Getter
    private PidAllocator pidAllocator;
    @Getter
    private final RealClock realClock = new RealClock();

    public SimulationBase(SimEntity parent) {
        super(parent);
        this.entityList = new ArrayList<>();
        this.eventsSchedule = new EventSchedule();
        this.simulationClock = 0;
        this.pidAllocator = new PidAllocator();
        this.state = EntityState.Run;
    }

    protected void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void addEntity(SimEntity entity) {
        entityList.add(entity);
    }

    @Override
    public void acceptEvent(Event evt) {
        lastClockOutputs.add(evt);
        setStateIfNotBlocked(EntityState.Run);
    }

    public void blackHoleEvent(Event event) {
        switch (event) {
            case org.pampasim.events.ProcessEvent processEvent:
                        logInfo("Processo com Pid " + processEvent.getProcess().getPid() +
                                " finalizou sua execução e foi terminado com sucesso");
                        break;
            default: logInfo("black hole got event " + event); break;
        }
    }

    public void scheduleToClock(int clock, final Event event) { // used to schedule events before the simulation starts
        eventsSchedule.schedule(clock, event);
        lastClockOutputs.add(event);
    }

    @Override
    public void run() {
        //  Run simulation, with steps:
        var phase1 = "1 - clear last inputs and outputs";
        var phase2 = "2 - gather all events for this clock from the schedule";
        var phase3 = "3 - send the gathered events to the right handler entities (event manager)";
        var phase4 = "4 - update idle states";
        var phase5 = "5 - check which kind of simulation tick we should perform, based on simulation state:";
        var phase5a = "5a - blocked tick, where all subordinate entities are either blocked or idle";
        // advances "real" time, all entities get run indiscriminately.
        var phase5b = "5b - running tick";
        // advances only the simulation time, only running entities get run.
        var phase5c = "5c - idle tick, waiting for \"real\" time events";
        // advances "real" time.
        var phase6 = "6 - compute the next state of the simulation based on the subordinate entities";

        SimEntity parent = getParent();
        StringBuilder info = new StringBuilder("running, entities: ");
        for (SimEntity entity : entityList) {
            info.append("\n").append(entity);
        }
        logInfo(info.toString());
        logInfo(phase1);
        lastClockInputs.clear();
        // Reuse memory, change var name mostly
        final List<Event> currentEvents = lastClockInputs;
        currentEvents.addAll(lastClockOutputs); // since we don't use the schedule for this no more
        lastClockOutputs.clear();

        logInfo(phase2);
        if (eventsSchedule.hasEventsFor(getRealClock().getTick())) {
            // checks the list of events that were queued before the simulation started, if there are ones to "arrive"
            // at this clock tick, add them to the list of events to be processed
            currentEvents.addAll(eventsSchedule.consume(getRealClock().getTick()));
        }
        logInfo("currentEvents = " + currentEvents);

        logInfo(phase3);
        // Necessary to create a copy to iterate over since handleEvent can add a KILL_PROCESS event
        // to the list as it's being iterated over
        currentEvents.stream()
                .filter(event -> !(event instanceof org.pampasim.events.Process.Kill)) // shouldn't be needed
                .forEach(eventManager::handleEvent); // processes all events except ProcessKill events


        logInfo(phase4);
        for (var entity : entityList) {
            entity.updateState();
        }

        logInfo(phase5);
        boolean isTopLevel = parent == null;
        if (getState() == EntityState.Blocked) {
            logInfo(phase5a);
            if (isTopLevel) {
                logInfo("top level block resolution");
                for (SimEntity entity : entityList) {
                    entity.clearBlock();
                    entity.run();
                }
                this.clearBlock();
                getRealClock().next();
            } else {
                logInfo("blocked simulation");
            }
        } else if (areAllEntitiesIdle() && hasPendingEvents()) {
            logInfo(phase5c);
            getRealClock().next();
        } else {
            logInfo(phase5b);
            executeRunnableEntities();
        }



        simulationClock += 1;

        logInfo(phase6);
        updateState();
    }

    private boolean areAllEntitiesIdle() {
        return entityList.stream().allMatch(entity -> entity.getState() == EntityState.Idle);
    }

    @Override
    public boolean shouldRunNextTick() {
        return !areAllEntitiesIdle() || this.hasPendingEvents();
    }

    @Override
    public void updateState() {
        boolean hasBlocked = entityList
                .stream()
                .anyMatch(entity -> entity.getState() == EntityState.Blocked);
        if (hasBlocked) {
            boolean allSettled = entityList.stream().allMatch(entity -> {
                EntityState state = entity.getState();
                return state == EntityState.Blocked
                        || state == EntityState.Idle;
            });
            if (allSettled) { this.state = EntityState.Blocked; }
            else { this.state = EntityState.Run; }
        } else {
            super.updateState();
        }
    }

    private void executeRunnableEntities() {
        for (SimEntity entity : entityList) {
            if (entity.getState() != EntityState.Run) { continue; }
            entity.run();
        }
    }

    public <T extends SimEntity> T getEntity(Class<T> entityClass) {
        return entityList.stream()
                .filter(entityClass::isInstance)
                .map(entityClass::cast)
                .findFirst()
                .orElse(null);
    }

    public boolean isFresh() {
        return this.eventsSchedule.isEmpty() && (getSimulationClock() == 0);
    }

    @Override
    public boolean hasPendingEvents() {
        // it really is off by one
        logInfo(eventsSchedule.toString() + " means any > " + getRealClock().getTick() + " == " + eventsSchedule.hasAnyAfter(getRealClock().getTick()-1));
        return !lastClockInputs.isEmpty() || !lastClockOutputs.isEmpty() || eventsSchedule.hasAnyAfter(getRealClock().getTick()-1);
    }

    // TODO: This references all the entity interfaces, might need decoupling
    public void applySpec(Spec s) {
        // only apply specs to a clean sim
        if (!isFresh()) {
            throw new RuntimeException("Can't apply spec to already running simulation!");
        }

        Spec.SchedulerInfo schedulerInfo = s.getSchedulerInfo();
        if (schedulerInfo != null) {
            Class<? extends Scheduler> schedulerClass = s.getSchedulerInfo().clazz();
            if (schedulerClass != null) try {
                Constructor<? extends Scheduler> cons = schedulerClass.getConstructor(Simulation.class);
                cons.newInstance(this);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No valid constructors for scheduler " + schedulerClass.getName() + ", error: " + e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error trying to instantiate scheduler: " + e);
            }
        }
        try {
            new Processor(this,
                    new ProcessorCore(
                            s.getProcessors()
                                    .getFirst() // Single processor, for now
                                    .coreCapacities()
                                    .getFirst() // Single core, for now
                    )
            );
        } catch (NoSuchElementException e) {
            System.out.println("Possible mistake: no processor set up by spec!");
        }

        if (s.isHasProcManager()) {
            new ProcessManager(this);
        } else {
            System.out.println("Possible mistake: no process manager set up by spec!");
        }
        this.pidAllocator = s.getPidAlloc();
        this.eventsSchedule = s.getEventSchedule();
    }

    @Override
    public boolean isStarted() {
        return !isFresh();
    }

    @Override
    public Simulation getSimulation() {
        return this;
    }

    @Override
    public Graph exportGraph() {
        String name = this.getClass().getSimpleName();
        List<Event> lastClockEvents = Stream.concat(lastClockInputs.stream(), lastClockOutputs.stream()).toList();
        boolean noEvents = lastClockEvents.isEmpty();
        String bufferTable= "<table border='0' cellborder='1' cellspacing='0'>\n" +
                (noEvents? "<tr><td>empty</td></tr>\n" :
                        lastClockEvents.stream()
                                .collect(StringBuilder::new,
                                        (acc, elem) ->
                                                acc.append("<tr><td port=\"evs")
                                                        .append(elem.getSerial())
                                                        .append("\">")
                                                        .append(elem)
                                                        .append("</td></tr>\n"),
                                        StringBuilder::append).toString()
                ) +
                "</table>\n";
        String htmlTable = "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td>" + name + "</td>" +
                "<td>Clock real " + this.getRealClock() + ", sim " + (this.getSimulationClock()-1) + "</td>" +
                "<td>" + this.getState() + "</td>" +
                "</tr>\n" +
                "<tr><td colspan='3' cellborder='0'>" + bufferTable + "</td></tr>\n" +
                "</table>\n";
        Node root = node(graphNodeName())
                .with(Shape.PLAIN_TEXT)
                .with(Label.html(htmlTable));
        Node nullNode = node("null").with(Shape.PLAIN_TEXT);
        Graph g = graph(name)
                .directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .with(root);
        // this probably needs to use the mutable graph api, to avoid too many allocs
        var entityGraphMap = this.entityList.stream().collect(
                () -> new HashMap<String, Graph>(),
                (map, entity) -> map.put(entity.graphNodeName(), entity.exportGraph()),
                HashMap::putAll);
        for(var subgraph : entityGraphMap.values()) {
            g = g.with(subgraph);
            g = g.with(root.link(to(subgraph).with(Style.INVIS)));
        }
        if (!noEvents) {
            for (Event ev : lastClockEvents.stream().toList()) {
                List<SimEntity> dsts = this.getEventManager().getAllDestinations(ev.getClass());
                for (var dstEntity : dsts) {
                    SimEntity source = ev.getSource();
                    if (source != null) {
                        g = g.with(
                                root.link(
                                        between(
                                                port("evs" + ev.getSerial(), Compass.EAST),
                                                //port("ev" + ev.getSerial(), Compass.WEST)
                                                entityGraphMap.get(dstEntity.graphNodeName()).asLinkTarget()
                                        )
                                ),
                                entityGraphMap.get(source != null ? source.graphNodeName() : nullNode).link(root)
                        );
                    }
                }
            }
        }
        return g;
    }

    @Override
    public String graphNodeName() {
        // WARN / FIXME: will name conflict if there are multiple entities of the same type in a simulation!
        // I'll let it be for now, since that edge case is very unlikely
        // ...this being the even more special case as the graph root
        return this.getClass().getSimpleName();
    }
}
