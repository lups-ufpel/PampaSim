package org.pampasim.Utils;

import java.util.LinkedList;
import java.util.Optional;

public class PidAllocator {

    public static final class Pid {
        long id;
        boolean inUse;

        public Pid(long id, boolean inUse) {
            this.id = id;
            this.inUse = inUse;
        }
        boolean isInUse() {
            return inUse;
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
    }
    private final int MAX_PID = Integer.MAX_VALUE;
    int lastPid = 0;
    private final LinkedList<Pid> pids;

    public PidAllocator() {
        pids = new LinkedList<>();
    }
    public Pid assignPid() {
        Pid pid;
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
