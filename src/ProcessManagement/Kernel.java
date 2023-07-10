package ProcessManagement;
import java.util.ArrayList;

public class Kernel {
    private ArrayList<PCB> procList;
    
    public Kernel() {
        procList = new ArrayList<PCB>();
    }

    public PCB getPCB(int index) {
        return procList.get(index);
    }

    public void newProcess() {
        procList.add(new PCB(procList.size()));
    }

    public ArrayList<PCB> getList() {
        ArrayList<PCB> clonedList = new ArrayList<PCB>();
        clonedList.addAll(procList);
        return clonedList;
    }
}
