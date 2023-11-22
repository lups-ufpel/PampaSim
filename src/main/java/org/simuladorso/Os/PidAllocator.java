package org.simuladorso.Os;

import java.util.LinkedList;
import java.util.Optional;
public class  PidAllocator{

    public static final class Pid{

        Integer id;
        Boolean inUse;

        public Pid(Integer id, boolean inUse){
            this.id = id;
            this.inUse = true;
        }
        boolean isInUse(){
            return inUse;
        }
        boolean isFree(){
            return !isInUse();
        }
        public void setFree(){
            this.inUse = false;
        }
        void setInUse(){
            this.inUse = true;
        }
        Integer getNum(){
            return id;
        }
    }

    private final int MAX_PID = 1000;
    int lastPid = 0;
    private final LinkedList<Pid> pidList;
    public PidAllocator(){
        pidList = new LinkedList<>();
    }
    public Pid AssignPid(){

        Pid pid;
        if(pidList.size() < MAX_PID){

            int curr_id = ++lastPid;
            pid = new Pid(curr_id, true);
            pidList.add(pid);
        }
        else{
            pid = recycleId();
            if(pid != null){
                pid.setInUse();
            }
            else {
                return null;
            }
        }
        return pid;
    }
    private Pid recycleId() {
        Optional<Pid> firstFreePid = pidList.stream()
                .filter(Pid::isFree)
                .findFirst();
        return firstFreePid.orElse(null);
    }
}
