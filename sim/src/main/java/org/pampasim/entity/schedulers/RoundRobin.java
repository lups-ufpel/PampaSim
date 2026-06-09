package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.resources.Process;

import java.util.ArrayDeque;

// Doesn't respect priorities! FIXME
// making it do so is nontrivial
@SuppressWarnings("unused")
public class RoundRobin extends Scheduler implements RespectsQuantum {
    ArrayDeque<Process> processDeque;

    public RoundRobin() {
        super();
        config.setFullyQualifiedClassName(getClass().getCanonicalName());
        processDeque = new ArrayDeque<>();
        processQueue = processDeque;
    }

    @Override
    public String shortDescription() {
        return "round-robin";
    }

    @Override
    public String longDescription() {
        return "Preemptive scheduler that enforces a hard time limit for the running process, the quantum. Otherwise behaves as first-come, first-served.";
    }

    @Override
    public void managedRun() {
        super.managedRun();
        // don't get me wrong, it will never be less than zero
        // BUT! think about the infinitesimal chance of a cosmic ray bitflip!
        if (lastRunProcess != null
                && lastRunProcess.getBurstTime() <= 0) {
            // Preempt!
            scheduleToNextClock(new org.pampasim.events.Process.Preemption(this, lastRunProcess));
        }
    }

    @Override
    protected void handleProcessSchedule(org.pampasim.events.Process.Schedule event) {
        processDeque.add(event.getProcess());
    }

    @Override
    protected Process nextProcessToSchedule() {
        Process p = processDeque.poll();
        // be sure to send it off with the proper burst time
        if (p != null) { p.setBurstTime(getQuantum()); } // This is incorrect
        // burst time is a measurable stat, not a remaining time counter we can set
        // ...right now this is a distinction without difference, but it will matter once
        // we bring code exec to the processes
        return p;
    }
}
