package org.pampasim.core;

public interface EventListener <T extends EventInfo> {
    void update(T eventInfo);
}
