package org.pampasim.resources.memory;

import lombok.Getter;
import org.pampasim.resources.Process;

import java.util.*;

public class FrameController {
    private final Map<Process, Set<Integer>> processtoFrameMap; // Optional: still useful for grouped access
    private final Map<Integer, PageTableEntry> frameToPageTableEntryMap; // Frame → PageTableEntry
    @Getter
    private final int totalFrames;
    private final List<PageTableEntry> frameAllocationList;

    public FrameController(int totalPages) {
        this.totalFrames = totalPages;
        this.processtoFrameMap = new HashMap<>();
        this.frameToPageTableEntryMap = new HashMap<>();
        this.frameAllocationList = new ArrayList<>(Collections.nCopies(totalPages, null));
    }

    public void allocateFrames(PageTableEntry entry, int frameAddress) {
        if (frameAddress < 0 || frameAddress >= totalFrames) {
            throw new IllegalArgumentException("Frame address out of bounds: " + frameAddress);
        }

        if (frameAllocationList.get(frameAddress) != null) {
            throw new IllegalStateException("Frame already allocated: " + frameAddress);
        }

        frameToPageTableEntryMap.put(frameAddress, entry);
        frameAllocationList.set(frameAddress, entry);

        Process process = entry.getProcess();
        processtoFrameMap
                .computeIfAbsent(process, k -> new HashSet<>())
                .add(frameAddress);

        entry.setValid(true); // Mark the page as valid
    }


    public void freeProcessFrames(Process process) {
        Set<Integer> frames = processtoFrameMap.remove(process);
        if (frames != null) {
            for (int frame : frames) {
                PageTableEntry entry = frameToPageTableEntryMap.remove(frame);
                if (entry != null) {
                    entry.setValid(false);
                }
                frameAllocationList.set(frame, null);
            }
        }
    }

    public void freeFrame(Integer frame) {
        PageTableEntry entry = frameToPageTableEntryMap.remove(frame);
        if (entry != null) {
            Process process = entry.getProcess();
            Set<Integer> frames = processtoFrameMap.get(process);
            if (frames != null) {
                frames.remove(frame);
                if (frames.isEmpty()) {
                    processtoFrameMap.remove(process);
                }
            }
            entry.setValid(false);
        }
        frameAllocationList.set(frame, null);
    }

    public boolean isFrameAllocated(int frame) {
        return frameToPageTableEntryMap.containsKey(frame);
    }

    public Process getFrameOwner(int frame) {
        PageTableEntry entry = frameToPageTableEntryMap.get(frame);
        return entry != null ? entry.getProcess() : null;
    }

    public Integer getPageNumber(int frame) {
        PageTableEntry entry = frameToPageTableEntryMap.get(frame);
        return entry != null ? entry.getPageNumber() : null;
    }

    public Set<Integer> getProcessFrames(Process process) {
        return processtoFrameMap.getOrDefault(process, Collections.emptySet());
    }

    public boolean hasFreeFrames() {
        return (totalFrames - frameToPageTableEntryMap.size()) > 0;
    }

    public int getTotalAllocatedFrames() {
        return frameToPageTableEntryMap.size();
    }

    public int getTotalProcessFrames(Process process) {
        Set<Integer> frames = processtoFrameMap.get(process);
        return frames != null ? frames.size() : 0;
    }

    public OptionalInt findFirstContiguousFreeRange(int rangeSize) {
        if (rangeSize <= 0 || rangeSize > totalFrames) {
            return OptionalInt.empty();
        }

        int consecutiveFree = 0;
        int start = -1;

        for (int frame = 0; frame < totalFrames; frame++) {
            if (frameAllocationList.get(frame) == null) {
                if (consecutiveFree == 0) {
                    start = frame;
                }
                consecutiveFree++;
                if (consecutiveFree == rangeSize) {
                    return OptionalInt.of(start);
                }
            } else {
                consecutiveFree = 0;
            }
        }

        return OptionalInt.empty();
    }

    public boolean canAccess(Process process, int frame) {
        PageTableEntry entry = frameToPageTableEntryMap.get(frame);
        return entry != null && entry.getProcess().equals(process);
    }

    public List<Process> getFrameAllocationList() {
        return frameAllocationList.stream()
                .map(entry -> entry != null ? entry.getProcess() : null)
                .toList();
    }

    public boolean isFrameFree(int frame) {
        return frameAllocationList.get(frame) == null;
    }

    public Process getFrameOwnerProcess(int frame) {
        PageTableEntry entry = frameAllocationList.get(frame);
        return entry != null ? entry.getProcess() : null;
    }

    public List<PageTableEntry> getRawFrameAllocationList() {
        return Collections.unmodifiableList(frameAllocationList);
    }

    public int size() {
        return frameAllocationList.size();
    }

}
