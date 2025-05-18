package org.pampasim.core;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.core.events.*;

import java.util.*;
import java.util.function.Consumer;

public abstract class EventManager {
    protected final Map<Class<? extends Event>, SimEntity> handlers;
    protected final Map<Class<? extends Event>, Class<? extends Event>> translations;
    protected final Map<Class<? extends Event>, Boolean> takesTime;
    private static long eventSerialCounter;
    protected final Simulation simulation;
    protected final
        Map<Class<? extends Event>, Set<Consumer<Event>>>
            snoopers = new HashMap<>();

    public EventManager(Simulation simulation) {
        handlers = new HashMap<>();
        translations = new HashMap<>();
        takesTime = new HashMap<>();
        this.simulation = simulation;
        setupHandlers();
        setupTranslations();
        setupFlags();
    }

    public abstract void setupHandlers();
    public abstract void setupTranslations();
    public abstract void setupFlags();

    public void addEventHandler(Class<? extends Event> eventClass, SimEntity handler) {
        if (handlers.containsKey(eventClass)) {
            throw new IllegalArgumentException("A handler for event type " + eventClass.getSimpleName() + " already exists.");
        }
        handlers.put(eventClass, handler);
    }

    public void addTranslation(Class<? extends Event> eventClass, Class<? extends Event> translationClass) {
        translations.put(eventClass, translationClass);
    }

    /*
        Method to call the accept method for the registered handler of the event type. If there is no handler,
        it attempts to translate it to a different event type. If there is no translation, an exception is thrown
     */
    public void handleEvent(Event event) {
        SimEntity handler = null;
        do {
            if (event == null) { return; }
            handler = handlers.get(event.getClass());
            if (handler == null) {
                event = translateEvent(event);
            }
        } while (handler == null);
        handler.acceptEvent(event);

        Event finalEvent = event;
        snoopers.entrySet()
                .stream()
                .filter(e -> e.getKey().isInstance(finalEvent))
                .forEach(e -> {
                    for (var snooper : e.getValue()) {
                        snooper.accept(finalEvent);
                    }
                });
    }

    protected Event translateEvent(Event event) {
        if (!translations.containsKey(event.getClass())) {
            throw new IllegalArgumentException("No event translation for: " + event);
        } else {
            var translationClass = translations.get(event.getClass());
            try {
                var translated = event.cloneAs(translationClass);
                System.out.println("Event translated: " + event + " to " + translated);
                return translated;
            } catch (IncompatibleEventDataException e) {
                throw new RuntimeException(
                        "Error trying to translate "
                                + event + ": " + e);
            }
        }
    }

    public List<SimEntity> getAllDestinations(Class<? extends Event> eventClass) {
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

    public boolean eventTakesTime(Event ev) {
        return takesTime.getOrDefault(ev.getClass(), false);
    }

    public void addSnooper(Class<? extends Event> eventClass, Consumer<Event> callback) {
        snoopers.compute(eventClass, (key, val)
                -> {
            if (val != null) { val.add(callback); }
            else { val = new HashSet<>(List.of(callback)); }
            return val;
        });
    }
}
