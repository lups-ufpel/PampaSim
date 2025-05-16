package org.pampasim.core.dsl.spec;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.EventSchedule;
import org.pampasim.core.events.*;
import org.pampasim.core.entity.Schedulers.FCFS;
import org.pampasim.core.entity.Schedulers.Scheduler;
import org.pampasim.core.resources.Process;
import org.pampasim.core.utils.PidAllocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// Data to set up a simulation scenario
/// Usually comes from a spec file
@Getter
public class Spec {
    // This string-based programming is really awkward, but needed:
    // can't instantiate a SimEntity (Scheduler) without having a simulation ready
    public record SchedulerInfo(Class<? extends Scheduler> clazz, Optional<Integer> quantum) {};
    @Setter
    private SchedulerInfo schedulerInfo;
    public record ProcessorInfo(ArrayList<Integer> coreCapacities) {};
    @Setter
    private ArrayList<ProcessorInfo> processors;
    @Setter
    private boolean hasProcManager;
    private PidAllocator pidAlloc; // and this is a bodge to just make the PIDs work for now
    private EventSchedule eventSchedule;
    private Map<Process, Color> colorMap;

    public Spec() {
        this(new SchedulerInfo(FCFS.class, Optional.of(0)));
    }

    public Spec(SchedulerInfo schedulerInfo) {
        this.schedulerInfo = schedulerInfo;
        this.pidAlloc = new PidAllocator();
        this.eventSchedule = new EventSchedule();
        this.colorMap = new HashMap<>();
        this.processors = new ArrayList<>();
        this.hasProcManager = false;
    }

    public Event addProcessArrival(Process p) {
        // color isn't a member of the Process class
        // for separation of concerns reasons? between the UI and the Sim
        // either way, that means we can't set the color here
        // colors from the spec ain't supported yet
        // which is a bummer
        var ev = new ProcessArrival(null, p);
        var arrivalTime = p.getArrivalTime();
        eventSchedule.schedule(arrivalTime, ev);
        return ev;
    }

    public void setSchedulerInfo(String name, Optional<Integer> quantum) {
        try (ScanResult scanResult =
                     new ClassGraph()
                             .verbose()
                             //.enableClassInfo()
                             .enableAllInfo()
                             .acceptPackages("org.pampasim") // dunno if needed
                             .scan()
        ) {
            ClassInfoList schedulerClasses
                    = scanResult.getSubclasses(Scheduler.class.getName());
            ClassInfo schedulerInfo = schedulerClasses.filter(clazz -> clazz.getName().contains(name)).getFirst();
            if (schedulerInfo == null) { throw new RuntimeException("scheduler " + name + " not found!"); }
            try {
                setSchedulerInfo(
                    new SchedulerInfo((Class<? extends Scheduler>) schedulerInfo.loadClass(), quantum)
                );
            } catch (ClassCastException e) {
                throw new RuntimeException("type cast error during scheduler class load: " + e);
            }
        }
    }

    @Override
    public String toString() {
        return "Spec with " + this.eventSchedule;
    }
}
