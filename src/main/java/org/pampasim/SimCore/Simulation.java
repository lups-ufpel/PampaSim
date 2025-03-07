package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;

public interface Simulation {
    void addEntity(PampaSimEntity entity);
    void scheduleToNextClock(final PampaSimEvent event);
    boolean runClockAndProcessEvents();
    <T extends PampaSimEntity> T getEntity(Class<T> entityClass);
    EventManager getEventManager();
}
