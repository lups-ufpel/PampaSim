package org.pampasim.core;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Compass;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.events.Event;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.utils.PidAllocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public abstract class SimulationBase extends AbstractSimEntity implements Simulation {
    private static final Logger LOGGER = LogManager.getLogger(SimulationBase.class);
    protected final ArrayList<SimEntity> entityList;
    protected EventSchedule eventsSchedule;
    protected final List<Event> lastClockInputs = new ArrayList<>();
    protected final List<Event> lastClockOutputs = new ArrayList<>();
    @Getter
    protected EventManager eventManager;
    private boolean clearBlock;
    @Getter
    protected int simulationClock;
    @Getter
    protected PidAllocator pidAllocator;
    @Getter
    protected final RealClock realClock = new RealClock();

    public SimulationBase(SimEntity parent) {
        super(parent);
        this.entityList = new ArrayList<>();
        this.eventsSchedule = new EventSchedule();
        this.simulationClock = 0;
        this.pidAllocator = new PidAllocator();
        this.state = EntityState.Run;
        this.clearBlock = false;
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

    public void scheduleToClock(int clock, final Event event) { // used to schedule events before the simulation starts
        eventsSchedule.schedule(clock, event);
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
        LOGGER.trace(info.toString());
        LOGGER.trace(phase1);
        lastClockInputs.clear();
        // Reuse memory, change var name mostly
        final List<Event> currentEvents = lastClockInputs;
        currentEvents.addAll(lastClockOutputs); // since we don't use the schedule for this no more
        lastClockOutputs.clear();

        LOGGER.trace(phase2);
        if (eventsSchedule.hasEventsFor(getRealClock().getTick())) {
            // checks the list of events that were queued before the simulation started, if there are ones to "arrive"
            // at this clock tick, add them to the list of events to be processed
            currentEvents.addAll(eventsSchedule.consume(getRealClock().getTick()));
        }
        LOGGER.trace("currentEvents = {}", currentEvents);

        LOGGER.trace(phase3);
        currentEvents
                .forEach(eventManager::handleEvent); // processes all events


        LOGGER.trace(phase4);
        for (var entity : entityList) {
            entity.updateState();
        }

        LOGGER.trace(phase5);
        boolean isTopLevel = parent == null;
        if (getState() == EntityState.Blocked) {
            LOGGER.trace(phase5a);
            if (isTopLevel) {
                LOGGER.trace("top level block resolution");
                for (SimEntity entity : entityList) {
                    entity.clearBlock();
                    entity.run();
                }
                this.clearBlock();
                getRealClock().next();
            } else if (clearBlock) { // top level simulation doesn't make use of the clear block flag
                for (SimEntity entity : entityList) {
                    entity.clearBlock();
                    entity.run();
                }
                clearBlock = false;
            } else {
                LOGGER.trace("blocked simulation");
            }
        } else if (areAllEntitiesIdle() && hasPendingEvents()) {
            LOGGER.trace(phase5c);
            getRealClock().next();
        } else {
            LOGGER.trace(phase5b);
            executeRunnableEntities();
        }



        simulationClock += 1;

        LOGGER.trace(phase6);
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

    @Override
    public boolean hasPendingEvents() {
        // it really is off by one
        LOGGER.trace("{} means any > {} == {}",
                eventsSchedule.toString(), getRealClock().getTick(),eventsSchedule.hasAnyAfter(getRealClock().getTick()-1));
        return !lastClockInputs.isEmpty() || !lastClockOutputs.isEmpty() || eventsSchedule.hasAnyAfter(getRealClock().getTick()-1);
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

    @Override
    public void clearBlock() {
        super.clearBlock();
        clearBlock = true;
    }

}
