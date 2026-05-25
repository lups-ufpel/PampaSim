package org.pampasim.resources.memory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.ProcessModuleInfo;
import org.pampasim.resources.Process;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Getter
public class ProcessMemoryInfo extends ProcessModuleInfo {
    @Data
    public static class MemoryConfigData { // info defined for all processes during the setup
        private final int swappingOperationsLength;
        private final int maxFrames;
        private final int workingSetWindow;
    }

    private final Process process;
    private final MemoryProcessCreationData creationData;
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
    private int pageHits;
    private int pageFaults;
    private final Map<Integer, Integer> runtimeIoOperationSchedule = new HashMap<>();
    private final ArrayList<Integer> runtimeAddressAccessList = new ArrayList<>();
    private final ArrayList<Boolean> runtimeModifyPage = new ArrayList<>();
    private final ArrayList<Boolean> runtimeFileBackedPages = new ArrayList<>();
    private int ioWaitingTime;

    public enum IoOperationType {
        DISK_ACCESS,
        PAGE_FAULT
    }

    public ProcessMemoryInfo(Process process, MemoryProcessCreationData creationData, MemoryConfigData memoryConfigData) {
        this.process = process;
        this.creationData = creationData;
        this.memoryConfigData = memoryConfigData;
        this.maxFrames = memoryConfigData.getMaxFrames();
        this.currentIoOperation = null;
        this.workingSet = new ArrayList<>();
        this.currentIoOperationTimeRemaining = 0;
        this.pageHits = 0;
        this.pageFaults = 0;
        this.ioWaitingTime = 0;

        // Initialize runtime structures from creation data
        this.runtimeAddressAccessList
                .addAll(creationData.addressAccessList.address.stream()
                        .map(BigInteger::intValue).toList());
        var numAccesses = this.runtimeAddressAccessList.size();
        creationData.modifyPages.pageId.sort(Comparator.naturalOrder());
        creationData.fileBackedPages.pageId.sort(Comparator.naturalOrder());
        for (int i = 0; i < numAccesses; i++) {
            var access = (long)runtimeAddressAccessList.get(i);
            var m = creationData.getModifyPages().pageId.contains(access);
            this.runtimeModifyPage.add(m);
            var f = creationData.getFileBackedPages().pageId.contains(access);
            this.runtimeFileBackedPages.add(f);
        }
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
    }


    // Getters for creation data properties
    public Integer getSize() {
        return Math.toIntExact(creationData.pageCount);
    }

    public ArrayList<Boolean> getFileBackedPages() {
        return this.runtimeFileBackedPages;
    }

    public ArrayList<Boolean> getModifyPage() {
        return this.runtimeModifyPage;
    }

    public boolean isLoopAccessList() {
        return creationData.loopAccessList;
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