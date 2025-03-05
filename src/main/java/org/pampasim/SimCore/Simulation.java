package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;

public interface Simulation {
    void addEntity(PampaSimEntity entity);
    double getCpuClock();
    void scheduleToNextClock(final PampaSimEvent event);
    boolean runClockAndProcessEventsSync();
    boolean runClockAndProcessEvents();
    <T extends PampaSimEntity> T getEntity(Class<T> entityClass);
    EventManager getEventManager();
}
