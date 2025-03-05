package org.pampasim.SimEntity;

import lombok.Getter;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.PampaSimEvent;

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
    public void scheduleToNextClock(PampaSimEvent event) {
        System.out.println("["+this.getClass().getSimpleName()+"] Evento Enviado: Tipo: " + event.getEventType().name() + " com Serial: " + event.getSerial());
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
        System.out.println("["+this.getClass().getSimpleName()+"] Evento recebido: Tipo: " + event.getEventType().name() + " com Serial: " + event.getSerial());
        this.buffer.add(event);
    }

}
