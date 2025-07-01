package org.pampasim.dialog;

public record SchedulerSelectionRecord(String schedulerName,
                                       boolean preemptive,
                                       int     quantum) {}