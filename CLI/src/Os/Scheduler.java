package Os;
import java.util.List;
import Mediator.Mediator;

public abstract class Scheduler {
    
    final int DEFAULT_TIME_SLICE = 4;
    List<Process> readyList;
    List<Process> waitingList;
    List<Process> terminatedList;
    List<Process> newList;
    Process[] runningList;
    int[] clockCycles; //keep track how many clock cycles each process has already executed 
    int time_slice;// this is the default time slice for accounting each process usage in the CPU
    

    public Scheduler(List<Process> newList, 
                    List<Process> readyList, 
                    List<Process> waitingList, 
                    List<Process> terminatedList, 
                    int numCores,
                    Mediator mediator){
        this.newList = newList;
        this.readyList = readyList;
        this.waitingList = waitingList;
        this.terminatedList = terminatedList;
        runningList = new Process[numCores];
        clockCycles = new int[numCores];
        time_slice = DEFAULT_TIME_SLICE;
    }

    /**
     * This method will schedule the next process to run
     * every time it is called it will count the amount of clocks the process has
     * run,
     * when the amount of clock cycles is equal to the quantum, the process will be
     * moved to the waiting list
     * and the next process will be scheduled to run.
     * The numCores determine how many processes can run at the same time, that
     * means that
     * runningList.size() will never be greater than numCores.
     * The coreID will be equivalent to the index of the runningList and
     * clockCycles.
     * 
     * @return PCB[] runningList
     */
    public abstract Process[] schedule();

    protected void readyToRunning(int coreID) {
        if (readyList.isEmpty()) {

            // if the running process is not terminated, it will continue to run
            if (runningList[coreID].getState() == Process.State.TERMINATED) {
                terminatedList.add(runningList[coreID]);
                runningList[coreID] = null;
            }

        } else {
            if (runningList[coreID] != null) {
                runningList[coreID].setState(Process.State.READY);
                //Invoker.invoke("Process", new Message("setState", Process.State.READY, runningList[coreID]));
                readyList.add(runningList[coreID]);
            }

            runningList[coreID] = readyList.remove(0);
            runningList[coreID].setState(Process.State.RUNNING);
        }
        clockCycles[coreID] = 0;
    }
    protected void moveNewProcessesToReadyList() {
        for (int i = 0; i < newList.size(); i++) {
            Process process = newList.get(i);
            process.setState(Process.State.READY);
            readyList.add(process);
            newList.remove(i);
            i--;
        }
    }

    protected void moveWaitingProcessesToReadyList() {
        for (int i = 0; i < waitingList.size(); i++) {
            Process process = waitingList.get(i);
            if (process.getState() == Process.State.READY) {
                readyList.add(process);
                waitingList.remove(i);
                i--;
            }
        }
    }

    public void printLists() {
        System.out.println("Ready List:");
        for (Process process : readyList) {
            System.out.println(process.getPid() + " " + process.getState());
        }
        System.out.println("Waiting List:");
        for (Process process : waitingList) {
            System.out.println(process.getPid() + " " + process.getState());
        }
        System.out.println("Running List:");
        for (int i = 0; i < runningList.length; i++) {
            if (runningList[i] != null) {
                System.out.println("pid: " + runningList[i].getPid() + " clockCycles: " + clockCycles[i] + " "
                        + runningList[i].getState());
            }
        }
    }
}
