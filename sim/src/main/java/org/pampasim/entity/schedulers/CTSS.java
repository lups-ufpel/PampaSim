package org.pampasim.entity.schedulers;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.Event;
import org.pampasim.events.Process.Schedule;
import org.pampasim.resources.Process;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/// [Source material](https://web.archive.org/web/20090906104446/http://larch-www.lcs.mit.edu:8001/~corbato/sjcc62/)
@SuppressWarnings("unused")
public class CTSS extends Scheduler implements RespectsQuantum {
    // this structure is not compatible with the base class processQueue. we will implement the overrides
    private final ArrayList<ArrayDeque<Process>> prioLevels;

    public CTSS() {
        super();
        this.config.setFullyQualifiedClassName(getClass().getCanonicalName());
        this.prioLevels = new ArrayList<>();
        this.prioLevels.add(new ArrayDeque<>()); // level 0
    }

    @Override
    public String shortDescription() {
        return "compatible time-sharing system";
    }

    @Override
    public String longDescription() {
        return "Preemptive scheduler inspired on the Compatible Time-Sharing System from MIT. If a process takes more than 2^priority quanta to execute, its priority gets incremented so its next execution will have 2 times the quanta, but will come after shorter processes. Unique in that lower priorities come first.";
    }

    @Override
    protected void handleProcessSchedule(Schedule event) {
        var proc = event.getProcess();
        var priority = proc.getPriority();
        proc.setBurstTime((1 << (priority + 1)) * getQuantum());
        while (priority >= prioLevels.size()) {
            prioLevels.add(new ArrayDeque<>());
        }
        this.prioLevels.get(priority).add(proc);
        // we don't evict the current process on our variant
    }

    private Optional<Integer> lowestPopulated() {
       for (int i = 0; i < this.prioLevels.size(); i++) {
           var q = this.prioLevels.get(i);
           if (!q.isEmpty()) {
               return Optional.of(i);
           }
       }
       return Optional.empty();
    }

    @Override
    public void managedRun() {
        buffer.forEach(this::processEvent);
        buffer.clear();

        Optional<org.pampasim.core.events.Event> preemptionEvent = Optional.empty();
        if (lastRunProcess != null
                && lastRunProcess.getBurstTime() <= 0) {
            var newPriority = lastRunProcess.getPriority() + 1;
            lastRunProcess.setPriority(newPriority);

            preemptionEvent = Optional.of(new org.pampasim.events.Process.Preemption(this, lastRunProcess));
        } else {
            super.managedRun();
        }
        preemptionEvent.ifPresent(this::scheduleToNextClock);
    }

    @Override
    public boolean shouldRunNextTick() {
        return !this.buffer.isEmpty()
                || (this.lastProcessFinished()
                    && this.prioLevels.stream().anyMatch(Predicate.not(ArrayDeque::isEmpty)));
    }

    @Override
    protected Process nextProcessToSchedule() {
        return this.lowestPopulated()
                .map(i -> this.prioLevels.get(i).poll())
                .orElse(null);
    }

    @Override
    public Stream<Process> getScheduledProcesses() {
        return this.prioLevels.stream().flatMap(Collection::stream);
    }

    @Override
    public void incrementWaitingTimes() {
        this.prioLevels.forEach(level -> level.forEach(Process::forwardWaitingTime));
    }
}
