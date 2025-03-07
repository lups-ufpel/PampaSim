package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<EventType, PampaSimEntity> handlers;
    private final Map<EventType, EventType> translations;
    private Simulation simulation;

    public EventManager(Simulation simulation) {
        handlers = new HashMap<>();
        translations = new HashMap<>();
        this.simulation = simulation;

        // FIXME: Temporary, adding event translations manually so that testing can be done
        translations.put(EventType.ALLOCATE_PROCESS, EventType.READY_PROCESS);
        translations.put(EventType.END_PROCESS, EventType.KILL_PROCESS);
        translations.put(EventType.DISPATCH_PROCESS, EventType.RUN_PROCESS);
        translations.put(EventType.IO_OPERATION, EventType.SCHEDULE_PROCESS);
    }

    public void addEventHandler(EventType eventType, PampaSimEntity handler) {
        if (handlers.containsKey(eventType)) {
            throw new IllegalArgumentException("A handler for event type " + eventType + " already exists.");
        }
        handlers.put(eventType, handler);
    }

    public void addTranslation(EventType eventType, EventType translation) {
        translations.put(eventType, translation);
    }

    /*
        Method to call the accept method for the registered handler of the event type. If there is no handler,
        it attempts to translate it to a different event type. If there is no translation, an exception is thrown
     */
    public void handleEvent(PampaSimEvent event) {
        PampaSimEntity handler = null;
        do {
            handler = handlers.get(event.getEventType());
            if (handler == null) {
                event = translateEvent(event);
                if (event.getEventType() == EventType.KILL_PROCESS) {
                    simulation.scheduleToNextClock(event);
                    return;
                }
            }
        } while (handler == null);

        handler.acceptEvent(event);
    }

    private PampaSimEvent translateEvent(PampaSimEvent event) {
        if (!translations.containsKey(event.getEventType())) {
            throw new IllegalArgumentException("No event translation for: " + event.getEventType());
        } else {
            System.out.println("Event translated: " + event.getEventType() + " to " + translations.get(event.getEventType()));
            return event.changeType(translations.get(event.getEventType()));
        }
    }

    public List<PampaSimEntity> getAllDestinations(EventType event) {
        return handlers.entrySet().stream()
                .filter(entry -> entry.getKey() == event)
                .map(Map.Entry::getValue)
                .toList();
    }
}
