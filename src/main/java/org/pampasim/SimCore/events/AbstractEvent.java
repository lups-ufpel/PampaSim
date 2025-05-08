package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import lombok.Getter;

import java.util.Comparator;

@Getter
public abstract class AbstractEvent implements Event, Comparable<Event> {
    private final PampaSimEntity source;
    private final long serial;
    private final int creationTick;

    public AbstractEvent(PampaSimEntity source) {
        var sim = source.getSimulation();
        this.source = source;
        this.serial = sim.getEventManager().nextEventSerial();
        this.creationTick = sim.getSimulationClock();
    }


    @Override
    public int compareTo(Event event) {
        return (int)(this.getSerial() - event.getSerial());
    }

    public String toString() {
        return "Event " + getClass().getSimpleName() + " #" + getSerial();
    }
}