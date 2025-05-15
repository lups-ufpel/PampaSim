package org.pampasim.SimCore.events;

import lombok.Getter;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
public class ProcessEvent extends AbstractEvent {
    private final Process process;
    public ProcessEvent(SimEntity source, Process proc) {
        super(source);
        process = proc;
    }

    @Override
    public Event cloneAs(Class<? extends Event> asClass) throws IncompatibleEventDataException {
        try {
            Constructor<? extends Event> cons = asClass.getConstructor(SimEntity.class, Process.class);
            return cons.newInstance(getSource(), getProcess());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException _e) {
            throw new IncompatibleEventDataException();
        }
    }

    public Object getData() {
        return getProcess();
    }
}

