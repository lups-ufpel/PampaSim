package org.pampasim.SimEntity;

import lombok.Getter;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Graph;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import static guru.nidi.graphviz.model.Factory.*;

import java.util.LinkedList;
import java.util.Queue;

import static java.util.Objects.requireNonNullElse;

public class PampaSimEntity implements SimEntity {

    @Getter
    private final Simulation simulation;
    private State state;
    protected Queue<PampaSimEvent> buffer;

    public PampaSimEntity(Simulation simulation) {
        this.simulation = simulation;
        state = State.RUNNABLE;
        this.simulation.addEntity(this);
        logInfo("PampaSim entity created.");
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
    public void scheduleToNextClock(PampaSimEvent event) {
        logInfo("Evento Enviado: Tipo: " + event.getEventType().name() + " com Serial: " + event.getSerial());
        simulation.scheduleToNextClock(event);
    }
    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public SimEntity setState(State state) {
        this.state = requireNonNullElse(state, State.RUNNABLE);
        return this;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    public void processEvent(PampaSimEvent event) {}
    public void processEventsInBuffer() {
        buffer.forEach(this::processEvent);
        buffer.clear();
    }
    public void acceptEvent(org.pampasim.SimCore.PampaSimEvent event) {
        logInfo("Evento recebido: Tipo: " + event.getEventType().name() + " com Serial: " + event.getSerial());
        this.buffer.add(event);
    }

    public void logInfo(String info) {
        // delightfully devilish (and expensive, i think)
        System.out.println(
                "[" + getClass().getSimpleName() + " @ "
                        + getSimulation().getSimulationClock()
                        + "] " + info);
    }

    @Override
    public Graph exportGraph() {
        String name = this.getClass().getSimpleName();
        // FIXME: bufferDesc doesn't make much sense atm (refactor)
        //String bufferDesc = (this.lastProcessedEvent == null)? "empty" : this.lastProcessedEvent.toString();
        String htmlTable =
            "<table border='0' cellborder='1' cellspacing='0'>" +
                "<tr>" +
                    "<td>" + name + "</td>" +
                    "<td>State: " + this.getState() + "</td>" +
                "</tr>" +
                //"<tr><td colspan='2'>" + bufferDesc + "</td></tr>" +
            "</table>";
        return graph(graphNodeName())
                .with(node(Label.html(htmlTable)).with(Shape.PLAIN_TEXT));
    }

    @Override
    public String graphNodeName() {
        // WARN / FIXME: will name conflict if there are multiple entities of the same type in a simulation!
        // e.g. multiple schedulers or multiple process managers
        // I'll let it be for now, since that edge case is very unlikely
        return this.getClass().getSimpleName();
    }

}
