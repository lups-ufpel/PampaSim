package org.pampasim.SimEntity;

import lombok.Getter;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Graph;
import org.pampasim.SimCore.RealClock;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;
import static guru.nidi.graphviz.model.Factory.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PampaSimEntity implements SimEntity {
    @Getter
    protected EntityState state = EntityState.Idle;
    @Getter
    private final Simulation simulation;
    @Getter
    private final SimEntity parent;
    protected Queue<Event> buffer;
    protected List<Event> lastRunBuffer = List.of();

    public PampaSimEntity(SimEntity parent) {
        if (parent != null) {
            this.simulation = parent.getSimulation();
            this.simulation.addEntity(this);
            this.parent = parent;
        } else {
            this.parent = null;
            this.simulation = (Simulation)this; // this will fail if the entity is not a simulation, by design
        }
        logInfo("PampaSim entity " + getClass().getSimpleName() + " created.");
        this.buffer = new LinkedList<>();
    }
    @Override
    public final boolean start() {
        if(this.isStarted()) {
            return false;
        }
        startInternal();
        return true;
    }
    protected void startInternal() {
    };
    @Override
    public void scheduleToNextClock(Event event) {
        logInfo("Evento Enviado: Tipo: " + event.getClass().getSimpleName() + " com Serial: " + event.getSerial());
        simulation.acceptEvent(event);
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    public void processEvent(Event event) {}
    public final void run() {
        this.lastRunBuffer = buffer.stream().toList();
        innerRun();
        buffer.clear();
        setStateIfNotBlocked(EntityState.Idle);
    }
    protected void innerRun() {
        buffer.forEach(this::processEvent);
    }
    public void acceptEvent(Event event) {
        logInfo("Evento recebido: Tipo: " + event.getClass().getSimpleName() + " com Serial: " + event.getSerial());
        this.buffer.add(event);
        setStateIfNotBlocked(EntityState.Run);
        if (this.getSimulation().getEventManager().eventTakesTime(event)) {
            logInfo("Blocked on " + event);
            this.state = EntityState.Blocked;
        }
    }

    protected void setStateIfNotBlocked(EntityState newState) {
        if (getState() != EntityState.Blocked) {
            this.state = newState;
        }
    }

    public void clearBlock() {
        this.state = buffer.isEmpty()? EntityState.Idle : EntityState.Run;
    }

    public void logInfo(String info) {
        System.out.println(this + " " + info);
    }

    @Override
    public String toString() {
        Simulation sim = getSimulation();
        RealClock realClock = sim.getRealClock();
        String realClockTxt = (realClock != null)? String.valueOf(sim.getRealClock().getTick()) : "0";
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
