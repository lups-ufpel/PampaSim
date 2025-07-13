package org.pampasim.resources.memory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.ProcessModuleInfo;
import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final Map<Integer, Integer> runtimeIoOperationSchedule = new HashMap<>();
    private final ArrayList<ArrayList<Integer>> runtimeAddressAccessList = new ArrayList<>();
    private final ArrayList<ArrayList<Boolean>> runtimeModifyPage = new ArrayList<>();

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

        // Initialize runtime structures from creation data
        for (Integer address : creationData.addressAccessList) {
            this.runtimeAddressAccessList.add(new ArrayList<>(List.of(address)));
        }

        for (Boolean modifyFlag : creationData.modifyPage) {
            this.runtimeModifyPage.add(new ArrayList<>(List.of(modifyFlag)));
        }
    }

    // Access entries must be between 0 <= Access Entry <= size-1
    public void addAccessEntry(int index, ArrayList<Integer> accessList) {
        runtimeAddressAccessList.add(index, accessList);
    }

    public void removeAccessEntry(int index) {
        runtimeAddressAccessList.remove(index);
    }

    public void editAccessEntry(int index, ArrayList<Integer> accessList) {
        runtimeAddressAccessList.set(index, accessList);
    }

    public void forwardIoOperation() {
        if (currentIoOperationTimeRemaining > 0) {
            currentIoOperationTimeRemaining--;
        }
    }

    public ArrayList<Integer> getCurrentAccessList() {
        int nextAccessListIndex = getCurrentAccessListIndex();
        return nextAccessListIndex >= 0 ? runtimeAddressAccessList.get(nextAccessListIndex) : new ArrayList<>(List.of(0));
    }

    public ArrayList<Boolean> getCurrentModifyPageFlags() {
        int nextAccessListIndex = getCurrentAccessListIndex();
        return nextAccessListIndex >= 0 ? runtimeModifyPage.get(nextAccessListIndex) : null;
    }

    private int getCurrentAccessListIndex() {
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
}