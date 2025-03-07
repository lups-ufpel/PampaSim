package org.pampasim.SimCore;

import org.pampasim.SimCoreRefactor.Event;
import org.pampasim.SimCoreRefactor.EventManager;
import org.pampasim.SimEntity.PampaSimEntity;

public interface Simulation {
    void addEntity(PampaSimEntity entity);
    void scheduleToNextClock(final PampaSimEvent event);
    boolean runClockAndProcessEvents();
    <T extends PampaSimEntity> T getEntity(Class<T> entityClass);
    EventManager getEventManager();
}
