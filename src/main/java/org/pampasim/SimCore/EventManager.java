package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private Map<EventType, PampaSimEntity> handlers;
    private Map<EventType, EventType> translations;

    public EventManager() {
        handlers = new HashMap<>();
        translations = new HashMap<>();
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
            if (handler != null) {
                event = translateEvent(event);
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

}
