package org.pampasim.SimEntity;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Graph;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.Utils.GraphVisualizeable;
import static guru.nidi.graphviz.model.Factory.*;

import static java.util.Objects.requireNonNullElse;

public class PampaSimEntity implements SimEntity {

    private Simulation simulation;
    private State state;
    private PampaSimEvent buffer;
    private PampaSimEvent lastProcessedEvent;

    public PampaSimEntity(Simulation simulation) {
        this.simulation = simulation;
        state = State.RUNNABLE;
        this.simulation.addEntity(this);
        System.out.println("[" + this.getClass().getSimpleName() + "] PampaSim entity created.");
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
    public boolean schedule(PampaSimEvent evt) {
        simulation.send(evt);
        return true;
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

    public Simulation getSimulation () {
        return simulation;
    }

    @Override
    public void processEvent(PampaSimEvent evt) {
    }
    public void run() {
        if(buffer != null) {
            processEvent(buffer);
            lastProcessedEvent = buffer;
            buffer = null;
        }
    }
    public void setEventBuffer(PampaSimEvent evt) {
        this.buffer = evt;
    }

    @Override
    public Graph exportGraph() {
        String name = this.getClass().getSimpleName();
        String bufferDesc = (this.lastProcessedEvent == null)? "empty" : this.lastProcessedEvent.toString();
        String htmlTable =
            "<table border='0' cellborder='1' cellspacing='0'>" +
                "<tr>" +
                    "<td>" + name + "</td>" +
                    "<td>State: " + this.getState() + "</td>" +
                "</tr>" +
                "<tr><td colspan='2'>" + bufferDesc + "</td></tr>" +
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
}
