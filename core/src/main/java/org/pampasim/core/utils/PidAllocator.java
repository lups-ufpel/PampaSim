package org.pampasim.core.utils;

import lombok.Data;

import java.util.LinkedList;
import java.util.Optional;

public class PidAllocator {
    @Data
    public static final class Pid implements Comparable<Pid>  {
        final long id;
        boolean inUse;

        public Pid(long id, boolean inUse) {
            this.id = id;
            this.inUse = inUse;
        }
        boolean isFree() {
            return !isInUse();
        }
        public void setInUse() {
            this.inUse = true;
        }
        public void setFree() {
            this.inUse = false;
        }
        @Override
        public int compareTo(Pid other) {
            if (other == null) {
                return 1;
            }
            return Long.compare(this.id, other.id);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pid pid = (Pid) o;
            return id == pid.id;
        }
        @Override
        public int hashCode() {
            return Long.hashCode(id);
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }
    int lastPid = 0;
    private final LinkedList<Pid> pids;

    public PidAllocator() {
        pids = new LinkedList<>();
    }
    public Pid assignPid() {
        Pid pid;
        int MAX_PID = Integer.MAX_VALUE;
        if(lastPid < MAX_PID) {
            int curr_id = ++lastPid;
            pid = new Pid(curr_id, true);
            pids.add(pid);
        } else {
            //todo: handle pid overflow.
            return null;
        }
        return pid;
    }
    private Pid recyclePid() {
        Optional<Pid> firstFreePid = pids.stream()
                .filter(Pid::isFree).findFirst();
        return firstFreePid.orElse(null);
    }
}
