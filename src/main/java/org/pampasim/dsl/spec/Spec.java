package org.pampasim.dsl.spec;

import lombok.Getter;
import org.pampasim.SimCore.EventType;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimResources.Process;
import org.pampasim.Utils.PidAllocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/// Data to set up a simulation scenario
/// Usually comes from a spec file
@Getter
public class Spec {
    // This string-based programming is really awkward, but needed:
    // can't instantiate a SimEntity (Scheduler) without having a simulation ready
    String schedulerName;
    PidAllocator pidAlloc; // and this is a bodge to just make the PIDs work for now
    Map<Integer, ArrayList<PampaSimEvent>> eventSchedule;

    public Spec() {
        this("FCFS");
    }
    public Spec(String schedulerName) {
        this.schedulerName = schedulerName;
        pidAlloc = new PidAllocator();
        eventSchedule = new HashMap<>();
    }

    public PampaSimEvent addProcessArrival(Process p) {
        // color isn't a member of the Process class
        // for separation of concerns reasons? between the UI and the Sim
        // either way, that means we can't set the color here
        // colors from the spec ain't supported yet
        // which is a bummer
        var ev = new PampaSimEvent(p, EventType.PROCESS_ARRIVAL);
        var arrivalTime = p.getArrivalTime();

        eventSchedule.compute(arrivalTime, (key, val) -> {
            if (val != null) {
                val.add(ev);
                return val;
            } else {
                var arr = new ArrayList<PampaSimEvent>();
                arr.add(ev);
                return arr;
            }
        });
        return ev;
    }
}
