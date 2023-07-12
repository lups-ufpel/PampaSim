package Kernel;
import java.util.ArrayList;

public class Kernel {
    private ArrayList<Process> readyList;
    private ArrayList<Process> waitingList;
    private ArrayList<Process> terminatedList;
    private ArrayList<Process> newList;

    //this list shall not be modified by other classes
    private ArrayList<Process> procList; 
    
    public Kernel() {
        procList = new ArrayList<Process>();
        readyList = new ArrayList<Process>();
        waitingList = new ArrayList<Process>();
        terminatedList = new ArrayList<Process>();
        newList = new ArrayList<Process>();
        
    }

    public Process getProcess(int index) {
        return procList.get(index);
    }

    public void newProcess() {
        Process newProcess = new Process(procList.size());
        procList.add(newProcess);
        newList.add(newProcess);
    }

    /**
     * This method will return a clone of the process list
     * @return
     */
    public ArrayList<Process> getProcessList() {
        ArrayList<Process> clonedList = new ArrayList<Process>();
        clonedList.addAll(procList);
        return clonedList;
    }

    public ArrayList<Process> getReadyList() {
        return readyList;
    }

    public ArrayList<Process> getWaitingList() {
        return waitingList;
    }
    
    public ArrayList<Process> getNewList() {
        return newList;
    }

    public ArrayList<Process> getTerminatedList() {
        return terminatedList;
    }

}
