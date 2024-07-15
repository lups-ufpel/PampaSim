package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;

public interface Simulation {
    void addEntity(PampaSimEntity entity);
    double getClock();
    void send(final PampaSimEvent event);
    void start();
    <T extends PampaSimEntity> T getEntity(Class<T> entityClass);
}
