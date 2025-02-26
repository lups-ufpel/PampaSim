package org.pampasim.SimCoreRefactor;

import org.pampasim.SimEntity.PampaSimEntity;
import org.w3c.dom.Entity;

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
        handlers.put(eventType, handler);
    }

    public void addTranslation(EventType eventType, EventType translation) {
        translations.put(eventType, translation);
    }

    /*
        Method to call the accept method for the registered handler of the event type. If there is no handler,
        it attempts to translate it to a different event type. If there is no translation, an exception is thrown
     */
    public void handleEvent(Event event) {
        PampaSimEntity handler = null;
        do {
            handler = handlers.get(event.getEventType());
            if (handler != null) {
                translateEvent(event);
            }
        } while (handler == null);

        handler.acceptEvent(event);
    }

    private void translateEvent(Event event) {
        if (!translations.containsKey(event.getEventType())) {
            throw new IllegalArgumentException("No event translation for: " + event.getEventType());
        } else {
            event.setEventType(translations.get(event.getEventType()));
            System.out.println("Event translated: " + event.getEventType() + " to " + translations.get(event.getEventType()));
        }
    }

}
