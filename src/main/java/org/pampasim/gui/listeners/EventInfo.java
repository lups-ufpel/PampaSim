package org.pampasim.gui.listeners;

public interface EventInfo {

    /**
     * Get the listener that was notified about the event.
     */
    <T extends EventInfo> EventListener<T> getListener();

    /**
     * Get a EventInfo instance from the given parameters.
     *
     */
    static EventInfo of(final EventListener<EventInfo> listener){
        return new EventInfo() {
            @Override public EventListener<EventInfo> getListener() { return listener; }
        };
    }
}
