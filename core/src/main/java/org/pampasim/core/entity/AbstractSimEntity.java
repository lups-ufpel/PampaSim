package org.pampasim.core.entity;

import lombok.Getter;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Graph;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.StandardException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.RealClock;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import static guru.nidi.graphviz.model.Factory.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class AbstractSimEntity implements SimEntity {
    private final Logger LOGGER = LogManager.getLogger(AbstractSimEntity.class);
    @Getter
    protected EntityState state = EntityState.Idle;
    @Getter
    private Simulation simulation;
    @Getter
    @Setter
    private boolean clearBlock;
    @Getter
    private SimEntity parent;
    protected Queue<Event> buffer;
    protected Queue<Event> blockedBuffer;
    protected List<Event> lastRunBuffer = List.of();

    @StandardException
    public static class SimEntityBindException extends RuntimeException {}

    public AbstractSimEntity() {
        LOGGER.debug("PampaSim entity {} created.", getClass().getSimpleName());
        this.buffer = new LinkedList<>();
        this.blockedBuffer = new LinkedList<>();
    }

    public AbstractSimEntity(@NonNull SimEntity parent) {
        this();
        bind(parent);
    }
    /// returns success status
    public boolean bind(@NonNull SimEntity parent) {
        this.simulation = parent.getSimulation();
        if (this.simulation == null) {
            LOGGER.debug("PampaSim entity {} couldn't bind to {}, null simulation",
                    getClass().getSimpleName(), parent.getClass().getSimpleName());
            throw new SimEntityBindException();
        }
        this.simulation.addEntity(this);
        this.parent = parent;
        LOGGER.debug("PampaSim entity {} bound to {}", getClass().getSimpleName(), parent.getClass().getSimpleName());
        return true;
    }

    @Override
    public void scheduleToNextClock(Event event) {
        LOGGER.trace("{} tx {}",getClass().getSimpleName(), event);
        if (simulation != null) {
            simulation.acceptEvent(event);
        } else {
            LOGGER.trace("null simulation event reroute: {}", event);
            this.acceptEvent(event);
        }
    }

    public void processEvent(Event event) {}
    public void run() {
        LOGGER.trace("{} run", getClass().getSimpleName());
        this.lastRunBuffer = buffer.stream().toList();
        managedRun();
        buffer.clear();
    }
    public void eagerRun() {
        while (getState() == EntityState.Run) {
            run();
        }
    }
    protected void managedRun() {
        buffer.forEach(this::processEvent);
    }
    @Override
    public boolean shouldRunNextTick() {
        return !buffer.isEmpty();
    }

    @Override
    public void updateState() {
        if (this.shouldRunNextTick()) {
            state = EntityState.Run;
        } else {
            if (blockedBuffer.isEmpty()) {
                state = EntityState.Idle;
            } else
                state = EntityState.Blocked;
        }
    }
    public void acceptEvent(Event event) {
        if (!this.getSimulation().getEventManager().eventTakesTime(event)) {
            LOGGER.trace("{} rx {}",getClass().getSimpleName(), event);
            this.buffer.add(event);
        } else {
            LOGGER.trace("{} Accepted blocking event on {}",getClass().getSimpleName(), event);
            //this.state = EntityState.Blocked;
            this.blockedBuffer.add(event);
        }
    }

    protected void setStateIfNotBlocked(EntityState newState) {
        if (getState() != EntityState.Blocked) {
            if (getState() != newState) {
                LOGGER.trace(" {} transitioned to {}", getClass().getSimpleName(), newState);
            }
            this.state = newState;
        } else {
            LOGGER.trace("{} transition to {} blocked",getClass().getSimpleName(), newState);
        }
    }

    public void clearBlock() {
        LOGGER.trace("block cleared");
        this.state = EntityState.Idle;
        while (!blockedBuffer.isEmpty()) {
            this.buffer.add(blockedBuffer.poll());
        }
        updateState();
    }

    @Override
    public String toString() {
        Simulation sim = getSimulation();
        RealClock realClock = sim.getRealClock();
        String realClockTxt = (realClock != null)? String.valueOf(sim.getRealClock().get()) : "0";
        String simClockTxt = String.valueOf(sim.getSimulationClock());
        return "[" + getClass().getSimpleName()
                + " @ real clock "
                + realClockTxt
                + " and sim clock "
                + simClockTxt
                + " state "
                + this.getState()
                + "]";
    }

    @Override
    public Graph exportGraph() {
        String name = this.getClass().getSimpleName();
        boolean noEvents = lastRunBuffer.isEmpty();
        String bufferTable= "<table border='0' cellborder='1' cellspacing='0'>\n" +
                (noEvents? "<tr><td>empty</td></tr>\n" :
                        lastRunBuffer.stream()
                                .collect(StringBuilder::new,
                                        (acc, elem) ->
                                                acc.append("<tr><td port=\"ev")
                                                        .append(elem.getSerial())
                                                        .append("\">")
                                                        .append(elem)
                                                        .append("</td></tr>\n"),
                                        StringBuilder::append).toString()
                ) +
                "</table>\n";
        String htmlTable =
            "<table border='0' cellborder='1' cellspacing='0'>" +
                "<tr>" +
                    "<td>" + name + "</td>" +
                    "<td>" + this.getState() + "</td>" +
                "</tr>" +
                "<tr><td colspan='2' cellborder='0'>" + bufferTable + "</td>" +
                "</tr>" +
            "</table>";
        return graph(graphNodeName())
                .with(node(name).with(Label.html(htmlTable)).with(Shape.PLAIN_TEXT));
    }

    @Override
    public String graphNodeName() {
        // WARN / FIXME: will name conflict if there are multiple entities of the same type in a simulation!
        // e.g. multiple schedulers or multiple process managers
        // I'll let it be for now, since that edge case is very unlikely
        return this.getClass().getSimpleName();
    }

    @Override
    public Simulation getTopLevelSimulation() {
        SimEntity parent = getParent();
        if (parent != null) {
            return parent.getTopLevelSimulation();
        } else {
            return this.getSimulation();
        }
    }
}
