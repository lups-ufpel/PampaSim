package org.pampasim.SimEntity;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;

import static java.util.Objects.requireNonNullElse;

public class PampaSimEntity implements SimEntity {

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
}
