package org.pampasim.SimCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class EventSchedule {
    private final Map<Integer, ArrayList<PampaSimEvent>> map; // events that are queued to happen at a specific clock tick (PROCESS_ARRIVAL events)
    private final TreeSet<Integer> futureKeys; // used in the check if there are any "future" events after any given clock

    public EventSchedule() {
        map = new HashMap<>();
        futureKeys = new TreeSet<>();
    }

    public void schedule(int clock, final PampaSimEvent event) {
        if (!map.containsKey(clock)) {
            map.put(clock, new ArrayList<>());
            futureKeys.add(clock);
        }
        map.get(clock).add(event);
    }

    public java.util.Collection<ArrayList<PampaSimEvent>> values() {
        return map.values();
    }

    public boolean hasAnyAfter(int thresholdClockExclusive) {
        return futureKeys.higher(thresholdClockExclusive) != null;
    }

    public boolean hasEventsFor(int clock) {
        return map.containsKey(clock);
    }

    public ArrayList<PampaSimEvent> get(int clock) {
        return map.get(clock);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("schedule [");
        for (int i = futureKeys.getFirst(); i < futureKeys.getLast(); i++) {
            sb.append(i).append(": ").append(map.get(i));
            sb.append(", ");
        }
        sb.delete(sb.length()-2, sb.length());
        sb.append("]");
        return sb.toString();
    }
}
