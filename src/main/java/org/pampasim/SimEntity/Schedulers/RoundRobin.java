package org.pampasim.SimEntity.Schedulers;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.SimCore.EventType;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimResources.Process;

import java.util.ArrayDeque;
import java.util.Queue;

// Doesn't respect priorities! FIXME
// making it do so is nontrivial
public class RoundRobin extends Scheduler implements RespectsQuantum {
    public Queue<Process> rrQueue;
    @Getter
    @Setter
    int quantum;

    public RoundRobin(Simulation simulation) {
        super(simulation);
        rrQueue = new ArrayDeque<>();
    }

    @Override
    public void run() {
        super.run();
        // don't get me wrong, it will never be less than zero
        // BUT! think about the infinitesimal chance of a cosmic ray bitflip!
        if (lastRunningProcess != null
                && lastRunningProcess.getBurstTime() <= 0) {
            // Preempt!
            scheduleToNextClock(new PampaSimEvent(lastRunningProcess, EventType.PREEMPT_PROCESS));
        }
    }

    @Override
    protected void handleScheduleProcess(PampaSimEvent event) {
        rrQueue.add(event.getProcess());
    }

    @Override
    protected Process nextProcessToSchedule() {
        Process p = rrQueue.poll();
        // be sure to send it off with the proper burst time
        if (p != null) { p.setBurstTime(getQuantum()); }
        return p;
    }
}
