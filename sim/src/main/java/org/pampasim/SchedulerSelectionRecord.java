package org.pampasim;

public record SchedulerSelectionRecord(String schedulerName,
                                       boolean preemptive,
                                       int     quantum) {}