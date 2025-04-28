package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.Utils.PidAllocator;
import org.pampasim.dsl.spec.Spec;

public interface Simulation {
    void addEntity(PampaSimEntity entity);
    void scheduleToNextClock(final PampaSimEvent event);
    void scheduleToClock(int clock, PampaSimEvent event);
    boolean runClockAndProcessEvents();
    <T extends PampaSimEntity> T getEntity(Class<T> entityClass);
    EventManager getEventManager();
    PidAllocator getPidAllocator();
    int getSimulationClock();
    void applySpec(Spec s);
}
