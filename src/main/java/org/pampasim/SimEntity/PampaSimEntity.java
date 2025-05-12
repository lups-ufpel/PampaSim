package org.pampasim.SimEntity;

import lombok.Getter;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Graph;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;
import static guru.nidi.graphviz.model.Factory.*;

import java.util.LinkedList;
import java.util.Queue;

public class PampaSimEntity implements SimEntity {

    @Getter
    private final Simulation simulation;
    @Getter
    private final SimEntity parent;
    protected Queue<Event> buffer;

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
    public void run() {
        buffer.forEach(this::processEvent);
        buffer.clear();
    }
    public void acceptEvent(Event event) {
        logInfo("Evento recebido: Tipo: " + event.getClass().getSimpleName() + " com Serial: " + event.getSerial());
        this.buffer.add(event);
    }

    public void logInfo(String info) {
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
                "</tr>" +
                //"<tr><td colspan='2'>" + bufferDesc + "</td></tr>" +
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
