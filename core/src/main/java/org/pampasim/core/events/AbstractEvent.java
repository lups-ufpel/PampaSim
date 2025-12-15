package org.pampasim.core.events;

import org.pampasim.core.entity.AbstractSimEntity;
import lombok.Getter;
import org.pampasim.core.entity.SimEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
public abstract class AbstractEvent implements Event, Comparable<Event> {
    private final SimEntity source;
    private final long serial;
    private final int creationTick;

    public AbstractEvent(SimEntity source) {
        this.source = source;
        if (source != null) {
            var sim = source.getSimulation();
            this.serial = sim.getEventManager().nextEventSerial();
            this.creationTick = sim.getSimulationClock().get();
        } else { // botch to just punt the issue down the line
            this.serial = -1;
            this.creationTick = -1;
        }
    }


    @Override
    public int compareTo(Event event) {
        return (int)(this.getSerial() - event.getSerial());
    }

    public Event cloneAs(Class<? extends Event> asClass) throws IncompatibleEventDataException {
        try {
            Constructor<? extends Event> cons = asClass.getConstructor(SimEntity.class);
            return cons.newInstance(getSource());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException _e) {
            throw new IncompatibleEventDataException();
        }
    }

    public String toString() {
        return "Event " + getClass().getSimpleName() + " #" + getSerial();
    }
}