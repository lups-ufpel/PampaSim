package org.pampasim.resources.memory;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.ProcessModuleInfo;
import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ProcessMemoryInfo extends ProcessModuleInfo {
    Process process;
    private final Integer size; // total number of pages the process occupies
    @Setter
    private Integer virtualAddressStart; // where the start of the virtual address range is
    private final ArrayList<ArrayList<Integer>> addressAccessList;
    @Setter
    private boolean loopAccessList;
    // Schedule for the IO operations, the processor is responsible for looking at this map and sending out IO operation events
    private final Map<Integer, Integer> IoOperationSchedule;
    // current IO operation, the processor is responsible for setting this if there is an IO operation.
    // Otherwise, it is used for timing the delay of the IO operations needed to handle page faults
    @Setter
    private int currentIoOperation;


    public ProcessMemoryInfo(Process process, Integer size) {
        this.process = process;
        this.size = size; //TODO: Make the user able to define how many pages the process occupies
        this.IoOperationSchedule = new HashMap<>();
        this.virtualAddressStart = null;
        this.addressAccessList = new ArrayList<>();
        this.loopAccessList = false;
        this.currentIoOperation = 0;
    }

    public void addAccessEntry(int index, ArrayList<Integer> accessList) {
        addressAccessList.add(index, accessList);
    }

    public void removeAccessEntry(int index) {
        addressAccessList.remove(index);
    }

    public void editAccessEntry(int index, ArrayList<Integer> accessList) {
        addressAccessList.set(index, accessList);
    }

    public ArrayList<Integer> getCurrentAccessList() { // based on CurrExecTime
        int nextAccessListIndex = process.getCurrExecTime();
        int addressAccessListSize = addressAccessList.size();

        if (addressAccessListSize == 0) {
            return null; // No entries in the list
        }
        if (loopAccessList) {
            nextAccessListIndex %= addressAccessListSize; // Wrap around using modulo
        } else {
            if (nextAccessListIndex >= addressAccessListSize) {
                return null; // No memory access if out of bounds and looping disabled
            }
        }
        return addressAccessList.get(nextAccessListIndex);
    }

    public void scheduleIoOperation(int execTick, int ioOperationLength) {
        if (execTick >= 0 && ioOperationLength > 0 && execTick >= process.getBurstTime()) { // sanity checks to prevent future errors
            IoOperationSchedule.put(execTick, ioOperationLength);
        }
    }

    public void removeIoOperation(int execTick) {
        IoOperationSchedule.remove(execTick);
    }

    public Integer getIoOperation(int execTick) {
        return IoOperationSchedule.getOrDefault(execTick, null);
    }


}
