package org.pampasim.SimCore;

public interface EventListener <T extends EventInfo> {
    void update(T eventInfo);
}
