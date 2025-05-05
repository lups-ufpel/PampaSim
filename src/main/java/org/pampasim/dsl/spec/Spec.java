package org.pampasim.dsl.spec;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.SimCore.EventSchedule;
import org.pampasim.SimCore.EventType;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimResources.Process;
import org.pampasim.Utils.PidAllocator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/// Data to set up a simulation scenario
/// Usually comes from a spec file
@Getter
public class Spec {
    // This string-based programming is really awkward, but needed:
    // can't instantiate a SimEntity (Scheduler) without having a simulation ready
    @Setter
    private String schedulerName;
    public record ProcessorInfo(ArrayList<Integer> coreCapacities) {};
    @Setter
    private ArrayList<ProcessorInfo> processors;
    @Setter
    private boolean hasProcManager;
    private PidAllocator pidAlloc; // and this is a bodge to just make the PIDs work for now
    private EventSchedule eventSchedule;
    private Map<Process, Color> colorMap;

    public Spec() {
        this("FCFS");
    }

    public Spec(String schedulerName) {
        this.schedulerName = schedulerName;
        this.pidAlloc = new PidAllocator();
        this.eventSchedule = new EventSchedule();
        this.colorMap = new HashMap<>();
        this.processors = new ArrayList<>();
        this.hasProcManager = false;
    }

    public PampaSimEvent addProcessArrival(Process p) {
        // color isn't a member of the Process class
        // for separation of concerns reasons? between the UI and the Sim
        // either way, that means we can't set the color here
        // colors from the spec ain't supported yet
        // which is a bummer
        var ev = new PampaSimEvent(p, EventType.PROCESS_ARRIVAL);
        var arrivalTime = p.getArrivalTime();
        eventSchedule.schedule(arrivalTime, ev);
        return ev;
    }

    @Override
    public String toString() {
        return "Spec with " + this.eventSchedule;
    }
}
