package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;

public interface Event extends Comparable<Event> {
    PampaSimEntity getSource();
    long getSerial();
    int getCreationTick();
    Object getData();
}
