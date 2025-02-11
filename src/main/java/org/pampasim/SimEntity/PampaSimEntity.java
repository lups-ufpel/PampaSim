package org.pampasim.SimEntity;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.Graph;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.Utils.GraphVisualizeable;
import static guru.nidi.graphviz.model.Factory.*;

import static java.util.Objects.requireNonNullElse;

public class PampaSimEntity implements SimEntity, GraphVisualizeable {

    private Simulation simulation;
    private State state;
    private PampaSimEvent buffer;

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
            buffer = null;
        }
    }
    public void setEventBuffer(PampaSimEvent evt) {
        this.buffer = evt;
    }

    public Graph exportGraph() {
        String name = this.getClass().getSimpleName();
        String bufferDesc = (this.buffer == null)? "empty" : this.buffer.toString();
        String htmlTable = "<table border='0' cellborder='1' cellspacing='0'>" +
                "<tr><td>" + name + "</td></tr>" +
                "<tr><td>" + bufferDesc + "</td></tr>" +
                "</table>";
        return graph(name).with(node(Label.html(htmlTable)));
    }
}
