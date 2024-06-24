package org.pampasim.gui.listeners;

public interface EventListener <T extends EventInfo> {
    void update(T eventInfo);
}
