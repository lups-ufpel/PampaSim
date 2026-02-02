package org.pampasim.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.core.events.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

public abstract class EventManager {
    private final Logger LOGGER = LogManager.getLogger(EventManager.class);
    protected final Map<Class<? extends Event>, SimEntity> handlers;
    protected final Map<Class<? extends Event>, Class<? extends Event>> translations;
    protected final Map<Class<? extends Event>, Boolean> takesTime;
    private static long eventSerialCounter;
    protected final Simulation simulation;
    protected final Map<Class<? extends Event>, Set<Consumer<Event>>> snoopers = new HashMap<>();

    // Shared logging resources
    private static BufferedWriter EVENT_LOG_WRITER;
    private static final String LOG_FILE_NAME = "simulation_events.log";
    private static final Object FILE_LOCK = new Object();

    static {
        initializeLogFile();
    }

    private static void initializeLogFile() {
        try {
            Path logPath = Paths.get(LOG_FILE_NAME);
            EVENT_LOG_WRITER = new BufferedWriter(new FileWriter(logPath.toFile(), true));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                synchronized (FILE_LOCK) {
                    try {
                        if (EVENT_LOG_WRITER != null) {
                            EVENT_LOG_WRITER.close();
                        }
                    } catch (IOException e) {
                        LogManager.getLogger(EventManager.class)
                                .error("Failed to close event log file", e);
                    }
                }
            }));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize event log file", e);
        }
    }

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

    public void handleEvent(Event event) {
        SimEntity handler = null;
        do {
            if (event == null) { return; }

            logEvent("HANDLED", event.getClass(), event, null);

            handler = handlers.get(event.getClass());
            Event finalEvent = event;
            snoopers.entrySet()
                    .stream()
                    .filter(e -> e.getKey().isInstance(finalEvent))
                    .forEach(e -> {
                        for (var snooper : e.getValue()) {
                            snooper.accept(finalEvent);
                        }
                    });
            if (handler == null) {
                event = translateEvent(event);
            }
        } while (handler == null);
        handler.acceptEvent(event);
    }

    protected Event translateEvent(Event event) {
        if (!translations.containsKey(event.getClass())) {
            throw new IllegalArgumentException("No event translation for: " + event);
        } else {
            var translationClass = translations.get(event.getClass());
            try {
                var translated = event.cloneAs(translationClass);
                logEvent("TRANSLATED", event.getClass(), event, translationClass);
                LOGGER.trace("Event translated: {} to {}", event, translated);
                return translated;
            } catch (IncompatibleEventDataException e) {
                throw new RuntimeException(
                        "Error trying to translate "
                                + event + ": " + e);
            }
        }
    }

    private void logEvent(String action, Class<? extends Event> eventClass, Event event,
                          Class<? extends Event> translatedClass) {
        long tick = simulation.getRealClock().get();
        String managerType = this.getClass().getSimpleName();

        String logEntry = String.format("[TICK %d][%s] %s: %s%s - %s%n",
                tick,
                managerType,
                action,
                formatClassName(eventClass),
                (translatedClass != null) ? " -> " + formatClassName(translatedClass) : "",
                event.toString());

        synchronized (FILE_LOCK) {
            try {
                EVENT_LOG_WRITER.write(logEntry);
                EVENT_LOG_WRITER.flush();
            } catch (IOException e) {
                LOGGER.error("Failed to write to event log: {}", e.getMessage());
            }
        }
    }

    private String formatClassName(Class<?> clazz) {
        String[] parts = clazz.getCanonicalName().split("\\.");
        if (parts.length > 2) {
            return parts[parts.length-2] + "." + parts[parts.length-1];
        }
        return clazz.getCanonicalName();
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
        snoopers.compute(eventClass, (key, val) -> {
            if (val != null) { val.add(callback); }
            else { val = new HashSet<>(List.of(callback)); }
            return val;
        });
    }

    public void removeAllEntriesForEntity(SimEntity entity) {
        handlers.entrySet().removeIf(entry -> entry.getValue() == entity);

        snoopers.values().forEach(callbacks ->
                callbacks.removeIf(consumer -> {
                    try {
                        var method = consumer.getClass().getDeclaredMethod("accept", Event.class);
                        return method.getDeclaringClass().isAssignableFrom(entity.getClass());
                    } catch (Exception e) {
                        return false;
                    }
                })
        );
    }
}
