package org.pampasim.SimCore;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Compass;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkTarget;
import guru.nidi.graphviz.model.Node;
import lombok.Getter;
import org.pampasim.SimEntity.*;
import org.pampasim.SimEntity.Schedulers.Scheduler;
import org.pampasim.SimResources.ProcessorCore;
import org.pampasim.Utils.PidAllocator;
import org.pampasim.dsl.spec.Spec;
import org.pampasim.SimCore.events.Event;
import org.pampasim.SimCore.events.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public class PampaSim extends PampaSimEntity implements Simulation {
    protected final ArrayList<SimEntity> entityList;
    private EventSchedule eventsSchedule;
    private final List<Event> lastClockInputs = new ArrayList<>();
    private final List<Event> lastClockOutputs = new ArrayList<>();
    private final List<Event> finishedProcesses;
    @Getter
    private final EventManager eventManager;
    @Getter
    private int simulationClock;
    @Getter
    private PidAllocator pidAllocator;
    @Getter
    private final RealClock realClock = new RealClock();

    public PampaSim(SimEntity parent) {
        super(parent);
        this.entityList = new ArrayList<>();
        this.eventManager = new InterimEventManager(this);
        this.finishedProcesses = new ArrayList<>();
        this.eventsSchedule = new EventSchedule();
        this.simulationClock = 0;
        this.pidAllocator = new PidAllocator();
    }

    @Override
    public void addEntity(SimEntity entity) {
        entityList.add(entity);
    }

    @Override
    public void scheduleToNextClock(final Event event) {
        scheduleToClock(simulationClock+1, event);
    }

    @Override
    public void acceptEvent(Event evt) {
        scheduleToNextClock(evt); // I believe this is correct
        setStateIfNotBlocked(EntityState.Run);
    }

    public void scheduleToClock(int clock, final Event event) { // used to schedule events before the simulation starts
        eventsSchedule.schedule(clock, event);
        lastClockOutputs.add(event);
    }

    @Override
    public void innerRun() {
        SimEntity parent = getParent();
        logInfo("");
        lastClockInputs.clear();
        lastClockOutputs.clear();
        // Reuse memory, change var name mostly
        final List<Event> currentEvents = lastClockInputs;

        if (eventsSchedule.hasEventsFor(simulationClock)) {
            // checks the list of events that were queued before the simulation started, if there are ones to "arrive"
            // at this clock tick, add them to the list of events to be processed
            currentEvents.addAll(eventsSchedule.get(simulationClock));
        }

        // Necessary to create a copy to iterate over since handleEvent can add a KILL_PROCESS event
        // to the list as it's being iterated over
        currentEvents.stream()
                .filter(event -> !(event instanceof ProcessKill)) // shouldn't be needed
                .forEach(eventManager::handleEvent); // processes all events except ProcessKill events

        boolean isTopLevel = parent == null;
        if (!isTopLevel) {
            if (parent.getParent().getState() == EntityState.Blocked) {
                if (this.state != EntityState.Blocked) {
                    throw new RuntimeException("invalid blocked state");
                }
                for (SimEntity entity : entityList) {
                    entity.run();
                }
            }
            else { executeRunnableEntities(); }
        } else {
            if (getState() == EntityState.Blocked) {
                for (SimEntity entity : entityList) {
                    entity.clearBlock();
                    entity.run();
                }
                getRealClock().next();
            } else { executeRunnableEntities(); }
        }

        List<ProcessEvent> killProcessEvents = currentEvents.stream()
                .filter(event -> event instanceof ProcessKill)
                .map(e -> (ProcessEvent) e)
                .peek(event -> System.out.println("[PampaSim] Processo com Pid " + event.getProcess().getPid() +
                        " finalizou sua execução e foi terminado com sucesso"))
                .toList(); // collects and logs ProcessKill events

        finishedProcesses.addAll(killProcessEvents); // adds to the finished processes list

        simulationClock += 1;

        this.state = nextState();
    }

    private EntityState nextState() {
        boolean hasBlocked = entityList
                .stream()
                .anyMatch(entity -> entity.getState() == EntityState.Blocked);
        if (hasBlocked) {
            boolean allSettled = entityList.stream().allMatch(entity -> {
                EntityState state = entity.getState();
                return state == EntityState.Blocked
                        || state == EntityState.Idle;
            });
            if (allSettled) { return EntityState.Blocked; }
            else { return EntityState.Run; }
        } else {
            boolean hasEvents = this.hasPendingEvents();
            if (hasEvents) { return EntityState.Run; }
            else { return EntityState.Idle; }
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
        return !lastClockInputs.isEmpty() || !lastClockOutputs.isEmpty() || eventsSchedule.hasAnyAfter(getSimulationClock());
    }

    // TODO: This references all the entity interfaces, might need decoupling
    public void applySpec(Spec s) {
        // only apply specs to a clean sim
        if (!isFresh()) {
            throw new RuntimeException("Can't apply spec to already running simulation!");
        }

        Class<? extends Scheduler> schedulerClass = s.getSchedulerInfo().clazz();
        if (schedulerClass != null) try {
            Constructor<? extends Scheduler> cons = schedulerClass.getConstructor(Simulation.class);
            cons.newInstance(this);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No valid constructors for scheduler " + schedulerClass.getName() + ", error: " + e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error trying to instantiate scheduler: " + e);
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