package org.pampasim.entity.schedulers;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.Simulation;
import org.pampasim.resources.Process;

import java.util.ArrayDeque;
import java.util.Queue;

// Doesn't respect priorities! FIXME
// making it do so is nontrivial
public class RoundRobin extends Scheduler implements RespectsQuantum {
    @Getter
    @Setter
    int quantum;

    public RoundRobin(Simulation simulation) {
        super(simulation);
        processQueue = new ArrayDeque<>();
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
    public boolean shouldRunNextTick() {
        return super.shouldRunNextTick()
                || (this.lastProcessFinished() && !processQueue.isEmpty());
    }

    @Override
    protected void handleProcessSchedule(org.pampasim.events.Process.Schedule event) {
        ((Queue<Process>) processQueue).add(event.getProcess());
    }

    @Override
    protected Process nextProcessToSchedule() {
        Process p = ((Queue<Process>) processQueue).poll();
        // be sure to send it off with the proper burst time
        if (p != null) { p.setBurstTime(getQuantum()); }
        return p;
    }
}
