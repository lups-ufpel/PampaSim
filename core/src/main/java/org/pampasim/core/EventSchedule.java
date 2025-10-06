package org.pampasim.core;

import java.util.*;
import java.util.function.Predicate;

import org.pampasim.core.events.Event;

public class EventSchedule {
    private final Map<Integer, ArrayList<Event>> map; // events that are queued to happen at a specific clock tick (PROCESS_ARRIVAL events)
    private final TreeSet<Integer> futureKeys; // used in the check if there are any "future" events after any given clock

    public EventSchedule() {
        map = new HashMap<>();
        futureKeys = new TreeSet<>();
    }

    public EventSchedule(EventSchedule other) {
        this();
        other.entries().forEach(e -> {
            var clock = e.getKey();
            var events = e.getValue();
            events.forEach(event -> this.schedule(clock, event));
        });
    }

    public void schedule(int clock, final Event event) {
        if (!map.containsKey(clock)) {
            map.put(clock, new ArrayList<>());
            futureKeys.add(clock);
        }
        map.get(clock).add(event);
    }

    public Event removeFirstMatch(Predicate<Event> test) {
        for (var entry : map.entrySet()) {
            var array = entry.getValue();
            for (var event : array) {
                if (test.test(event)) {
                    array.remove(event);
                    if (array.isEmpty()) {
                        futureKeys.remove(entry.getKey());
                    }
                    return event;
                }
            }
        }
        return null;
    }

    public Set<Map.Entry<Integer, ArrayList<Event>>> entries() {
        return map.entrySet();
    }
    public java.util.Collection<ArrayList<Event>> values() {
        return map.values();
    }

    public boolean hasAnyAfter(int thresholdClockExclusive) {
        return futureKeys.higher(thresholdClockExclusive) != null;
    }

    public boolean hasEventsFor(int clock) {
        return map.containsKey(clock);
    }

    public ArrayList<Event> get(int clock) {
        return map.get(clock);
    }
    public ArrayList<Event> consume(int clock) {
        futureKeys.remove(clock);
        return map.remove(clock);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("schedule [");
        if (!futureKeys.isEmpty()) {
            for (int i = futureKeys.getFirst(); i <= futureKeys.getLast(); i++) {
                sb.append(i).append(": ").append(map.get(i));
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
}
