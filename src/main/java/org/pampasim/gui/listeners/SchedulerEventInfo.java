package org.pampasim.gui.listeners;

import org.pampasim.Os.Scheduler;

public interface SchedulerEventInfo extends EventInfo {

    Scheduler getScheduler();

    static SchedulerEventInfo of (final EventListener<? extends SchedulerEventInfo> listener, final Scheduler scheduler) {
        return new SchedulerEventInfo() {
            @Override public Scheduler getScheduler() {
                return scheduler;
            }
            @Override public EventListener<? extends EventInfo> getListener() {
                return listener;
            }
        };
    }
}
