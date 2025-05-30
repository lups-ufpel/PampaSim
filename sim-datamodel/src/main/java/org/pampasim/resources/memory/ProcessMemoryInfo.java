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
    private final Map<Integer, Integer> ioOperationSchedule;
    // current IO operation, the processor is responsible for setting this if there is an IO operation.
    // Otherwise, it is used for timing the delay of the IO operations needed to handle page faults
    //TODO: Add a field to define which pages are modifiable vs purely executable (to justify the dirty bit)
    @Setter
    private int currentIoOperationTimeRemaining;
    @Setter
    private IoOperationType currentIoOperation;
    private final int swappingOperationsLength; //how long the swapping operations take when a page fault happens
    @Setter
    private ProcessPageTable pageTable; // reference to the process' page table
    private int maxFrames; // how many frames this process can have in the main memory
    private final ArrayList<PageTableEntry> workingSet;
    private int referenceCounter; // reference counter for computing the working set
    private int workingSetWindow;

    public enum IoOperationType {
        /**
         * The resources.Process requested an IO operation that requires a disk access
         */
        DISK_ACCESS,

        /**
         * The resources.Process virtual memory access resulted in a Page Fault
         */
        PAGE_FAULT
    }

    public ProcessMemoryInfo(Process process, int size, int swappingOperationsLength, int maxFrames, int workingSetWindow) {
        this.process = process;
        this.size = size; //TODO: Make the user able to define how many pages the process occupies
        this.ioOperationSchedule = new HashMap<>();
        this.virtualAddressStart = null;
        this.addressAccessList = new ArrayList<>();
        this.loopAccessList = true;
        this.currentIoOperation = null;
        this.maxFrames = maxFrames;
        this.workingSet = new ArrayList<>();
        this.referenceCounter = 0;
        this.workingSetWindow = workingSetWindow;

        this.currentIoOperationTimeRemaining = 0;
        this.swappingOperationsLength = swappingOperationsLength;
    }

    // Access entries must be between 0 <= Access Entry <= size-1
    public void addAccessEntry(int index, ArrayList<Integer> accessList) {
        addressAccessList.add(index, accessList);
    }

    public void removeAccessEntry(int index) {
        addressAccessList.remove(index);
    }

    public void editAccessEntry(int index, ArrayList<Integer> accessList) {
        addressAccessList.set(index, accessList);
    }

    public void forwardIoOperation() {
        if (currentIoOperationTimeRemaining > 0) {
            currentIoOperationTimeRemaining--;
        }
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
            ioOperationSchedule.put(execTick, ioOperationLength);
        }
    }


    public void removeIoOperation(int execTick) {
        ioOperationSchedule.remove(execTick);
    }

    public Integer getScheduledIoOperation(int execTick) {
        return ioOperationSchedule.getOrDefault(execTick, null);
    }

    public void addMaxFrames() {
        maxFrames++;
    }
    public void subMaxFrames() {
        if (maxFrames > 1) {
            maxFrames--;
        }
    }

    public void computeWorkingSet() {
        ArrayList<PageTableEntry> referencedEntries = pageTable.getReferencedEntries();
        workingSet.clear();
        workingSet.addAll(referencedEntries);
        referencedEntries.forEach(pageTableEntry -> pageTableEntry.setReferenced(false));
        referenceCounter = 0;
    }

    public void registerReference() {
        referenceCounter++;
        if (referenceCounter >= workingSetWindow) {
            computeWorkingSet();
        }
    }


}
