package org.pampasim.resources.memory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.ProcessModuleInfo;
import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ProcessMemoryInfo extends ProcessModuleInfo {

    @Data
    public static class CreationData { // creation data unique to each process
        private final int size;
        private final ArrayList<Boolean> fileBackedPages;
        private final ArrayList<Integer> addressAccessList;
        private final ArrayList<Boolean> modifyPage;
        private final boolean loopAccessList;
        private final ArrayList<Integer> ioOperationSchedule;
    }

    @Data
    public static class MemoryConfigData { // info defined for all processes during the setup
        private final int swappingOperationsLength;
        private final int maxFrames;
        private final int workingSetWindow;
    }

    private final Process process;
    private final CreationData creationData;
    private final MemoryConfigData memoryConfigData;

    // Runtime state fields
    @Setter
    private int currentIoOperationTimeRemaining;
    @Setter
    private IoOperationType currentIoOperation;
    @Setter
    private ProcessPageTable pageTable;
    private int maxFrames;
    private final ArrayList<PageTableEntry> workingSet;
    private int referenceCounter;
    private int pageHits;
    private int pageFaults;
    private final Map<Integer, Integer> runtimeIoOperationSchedule = new HashMap<>();
    private final ArrayList<Integer> runtimeAddressAccessList = new ArrayList<>();
    private final ArrayList<Boolean> runtimeModifyPage = new ArrayList<>();
    private int ioWaitingTime;

    public enum IoOperationType {
        DISK_ACCESS,
        PAGE_FAULT
    }

    public ProcessMemoryInfo(Process process, CreationData creationData, MemoryConfigData memoryConfigData) {
        this.process = process;
        this.creationData = creationData;
        this.memoryConfigData = memoryConfigData;
        this.maxFrames = memoryConfigData.getMaxFrames();
        this.currentIoOperation = null;
        this.workingSet = new ArrayList<>();
        this.referenceCounter = 0;
        this.currentIoOperationTimeRemaining = 0;
        this.pageHits = 0;
        this.pageFaults = 0;
        this.ioWaitingTime = 0;

        // Initialize runtime structures from creation data
        this.runtimeAddressAccessList.addAll(creationData.addressAccessList);
        this.runtimeModifyPage.addAll(creationData.modifyPage);
    }

    public void forwardIoOperation() {
        if (currentIoOperationTimeRemaining > 0) {
            currentIoOperationTimeRemaining--;
        }
    }

    public Integer getCurrentAccess() {
        int nextAccessListIndex = getCurrentAccessIndex();
        return nextAccessListIndex >= 0 ? runtimeAddressAccessList.get(nextAccessListIndex) : 0;
    }

    public Boolean getCurrentModifyPageFlag() {
        int nextAccessListIndex = getCurrentAccessIndex();
        return nextAccessListIndex >= 0 ? runtimeModifyPage.get(nextAccessListIndex) : null;
    }

    private int getCurrentAccessIndex() {
        int nextAccessListIndex = process.getCurrExecTime();
        int addressAccessListSize = runtimeAddressAccessList.size();

        if (addressAccessListSize == 0) {
            return -1;
        }
        if (creationData.loopAccessList) {
            return nextAccessListIndex % addressAccessListSize;
        }
        return nextAccessListIndex < addressAccessListSize ? nextAccessListIndex : -1;
    }

    public void scheduleIoOperation(int execTick, int ioOperationLength) {
        if (execTick >= 0 && ioOperationLength > 0 && execTick >= process.getBurstTime()) {
            runtimeIoOperationSchedule.put(execTick, ioOperationLength);
        }
    }

    public void removeIoOperation(int execTick) {
        runtimeIoOperationSchedule.remove(execTick);
    }

    public Integer getScheduledIoOperation(int execTick) {
        return runtimeIoOperationSchedule.getOrDefault(execTick, null);
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
        if (referenceCounter >= memoryConfigData.getWorkingSetWindow()) {
            computeWorkingSet();
        }
    }

    // Getters for creation data properties
    public Integer getSize() {
        return creationData.size;
    }

    public ArrayList<Boolean> getFileBackedPages() {
        return creationData.fileBackedPages;
    }

    public ArrayList<Boolean> getModifyPage() {
        return creationData.modifyPage;
    }

    public boolean isLoopAccessList() {
        return creationData.loopAccessList;
    }

    public ArrayList<Integer> getIoOperationSchedule() {
        return creationData.ioOperationSchedule;
    }

    public void registerPageHit() {
        pageHits++;
    }

    public void registerPageFault() {
        pageFaults++;
    }

    public double getPageFaultRate() {
        int totalAccesses = pageHits + pageFaults;
        if (totalAccesses == 0) {
            return 0.0;
        }
        return (double) pageFaults / totalAccesses;
    }

    public void incrementIoWaitingTime() {
        ioWaitingTime++;
    }
}