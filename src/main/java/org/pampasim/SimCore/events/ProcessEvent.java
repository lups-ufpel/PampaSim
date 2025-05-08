package org.pampasim.SimCore.events;

import lombok.Getter;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
public class ProcessEvent extends AbstractEvent {
    private final Process process;
    public ProcessEvent(PampaSimEntity source, Process proc) {
        super(source);
        process = proc;
    }

    @Override
    public Event cloneAs(Class<? extends Event> otherClass) throws IncompatibleEventDataException {
        try {
            Constructor<? extends Event> cons = otherClass.getConstructor(PampaSimEntity.class, Process.class);
            return cons.newInstance(getSource(), getProcess());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException _e) {
            throw new IncompatibleEventDataException();
        }
    }

    public Object getData() {
        return getProcess();
    }
}

