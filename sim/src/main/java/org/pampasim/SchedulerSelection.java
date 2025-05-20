package org.pampasim;

public record SchedulerSelection(String schedulerName,
                                 boolean preemptive,
                                 int     quantum) {}