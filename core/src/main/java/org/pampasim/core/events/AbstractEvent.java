package org.pampasim.core.events;

import lombok.Setter;
import lombok.Getter;
import org.pampasim.core.entity.SimEntity;

import javax.xml.validation.Schema;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class AbstractEvent implements Event, Comparable<Event> {
    // note that some Event methods are fulfilled by lombok generated getters
    private final SimEntity source;
    private final long serial;
    @Setter
    private int intraTickOrder;
    protected static Map<Class<?>, Optional<Schema>> payloadSchemas;

    public AbstractEvent(SimEntity source) {
        this.source = source;
        if (source != null) {
            var sim = source.getSimulation();
            this.serial = sim.getEventManager().nextEventSerial();
        } else { // bodge to just punt the issue down the line
            this.serial = -1;
        }
        payloadSchemas = new HashMap<>();
    }


    @Override
    public int compareTo(Event event) {
        var tickWise = (int)(this.getSerial() - event.getSerial());
        var itoWise = this.getIntraTickOrder() - event.getIntraTickOrder();
        return (tickWise != 0)? tickWise : itoWise;
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