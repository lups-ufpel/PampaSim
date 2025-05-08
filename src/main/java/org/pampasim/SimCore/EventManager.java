package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimCore.events.Event;
import org.pampasim.SimCore.events.*;
import org.pampasim.SimEntity.Schedulers.Scheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<Class<? extends Event>, PampaSimEntity> handlers;
    private final Map<Class<? extends Event>, Constructor<? extends Event>> translations;
    private static long eventSerialCounter;
    private Simulation simulation;

    public EventManager(Simulation simulation) {
        handlers = new HashMap<>();
        translations = new HashMap<>();
        this.simulation = simulation;

        // FIXME: Temporary, adding event translations manually so that testing can be done
        addTranslation(ProcessAllocate.class, ProcessReady.class);
        addTranslation(ProcessEnd.class, ProcessKill.class);
        addTranslation(ProcessDispatch.class, ProcessRun.class);
        addTranslation(ProcessIoOperation.class, ProcessSchedule.class);
    }

    public void addEventHandler(Class<? extends Event> eventClass, PampaSimEntity handler) {
        if (handlers.containsKey(eventClass)) {
            throw new IllegalArgumentException("A handler for event type " + eventClass.getSimpleName() + " already exists.");
        }
        handlers.put(eventClass, handler);
    }

    public void addTranslation(Class<? extends Event> eventClass, Class<? extends Event> translationClass) {
        try {
            translations.put(eventClass, cons);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No valid constructors for event " + translationClass.getSimpleName() + ", error: " + e);
        }
    }

    /*
        Method to call the accept method for the registered handler of the event type. If there is no handler,
        it attempts to translate it to a different event type. If there is no translation, an exception is thrown
     */
    public void handleEvent(Event event) {
        PampaSimEntity handler = null;
        do {
            handler = handlers.get(event.getClass());
            if (handler == null) {
                event = translateEvent(event);
                if (event instanceof ProcessKill) {
                    simulation.scheduleToNextClock(event);
                    return;
                }
            }
        } while (handler == null);

        handler.acceptEvent(event);
    }

    private Event translateEvent(Event event) {
        if (!translations.containsKey(event.getClass())) {
            throw new IllegalArgumentException("No event translation for: " + event);
        } else try {
            var translationConstructor = translations.get(event.getClass());
            var translated = translationConstructor.newInstance(event);
            System.out.println("Event translated: " + event + " to " + translated);
            return translated;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(
                    "Error trying to translate "
                            + event + ": " + e);
        }
    }

    public List<PampaSimEntity> getAllDestinations(Class<? extends Event> eventClass) {
        return handlers.entrySet().stream()
                .filter(entry -> entry.getKey() == eventClass)
                .map(Map.Entry::getValue)
                .toList();
    }

    public long nextEventSerial() {
        var s = eventSerialCounter;
        eventSerialCounter++;
        return s;
    }
}
