package org.pampasim.resources.memory;

import lombok.Getter;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.resources.Process;

import java.util.*;

public class FrameController {
    private final Map<Process, Set<Integer>> processtoFrameMap; // Map to store which pages/frame are allocated to which Processes
    private final Map<Integer, Process> frametoProcessMap; // Map to quickly consult which page/frame a process owns
    @Getter
    private final int totalPages; // Total number of page/frames
    private final List<Process> frameAllocationList;

    public FrameController(int totalPages) {
        this.totalPages = totalPages;
        this.processtoFrameMap = new HashMap<>();
        this.frametoProcessMap = new HashMap<>();
        this.frameAllocationList = new ArrayList<>(Collections.nCopies(totalPages, null));
    }

    public boolean allocateFrames(Process process, int startPage, int endPage) {
        if (startPage < 0 || endPage >= totalPages || startPage > endPage) {
            return false;
        }

        // Check if any page in the range is already allocated
        for (int page = startPage; page <= endPage; page++) {
            if (frameAllocationList.get(page) != null) {
                return false;
            }
        }

        Set<Integer> pages = processtoFrameMap.computeIfAbsent(process, k -> new HashSet<>());

        for (int page = startPage; page <= endPage; page++) {
            pages.add(page);
            frametoProcessMap.put(page, process);
            frameAllocationList.set(page, process); // Mark as allocated
        }

        return true;
    }

    public void freeProcessFrames(Process process) {
        Set<Integer> pages = processtoFrameMap.remove(process);
        if (pages != null) {
            for (int page : pages) {
                frametoProcessMap.remove(page);
                frameAllocationList.set(page, null); // Mark as free
            }
        }
    }

    public void freeFrame(Integer pageFrame) {
        Process process = frametoProcessMap.remove(pageFrame);
        if (process != null) {
            Set<Integer> pages = processtoFrameMap.get(process);
            if (pages != null) {
                pages.remove(pageFrame);
                if (pages.isEmpty()) {
                    processtoFrameMap.remove(process);
                }
            }
            frameAllocationList.set(pageFrame, null); // Mark as free
        }
    }

    public boolean isFrameAllocated(int page) {
        return frametoProcessMap.containsKey(page);
    }

    public Process getFrameOwner(int page) {
        return frametoProcessMap.get(page);
    }

    public Set<Integer> getProcessFrames(Process process) {
        return processtoFrameMap.getOrDefault(process, Collections.emptySet());
    }

    public Map<Integer, Process> getAllocatedFrames() {
        return new HashMap<>(frametoProcessMap);
    }

    public boolean hasFreeFrames() {
        return (totalPages - frametoProcessMap.size()) > 0;
    }

    public int getTotalAllocatedFrames() {
        return frametoProcessMap.size();
    }

    public int getTotalProcessFrames(Process process) {
        Set<Integer> frames = processtoFrameMap.get(process);
        return frames != null ? frames.size() : 0;
    }

    public OptionalInt findFirstContiguousFreeRange(int rangeSize) {
        if (rangeSize <= 0 || rangeSize > totalPages) {
            return OptionalInt.empty();
        }

        int consecutiveFree = 0;
        int startPage = -1;

        for (int page = 0; page < totalPages; page++) {
            if (frameAllocationList.get(page) == null) {
                if (consecutiveFree == 0) {
                    startPage = page;
                }
                consecutiveFree++;
                if (consecutiveFree == rangeSize) {
                    return OptionalInt.of(startPage);
                }
            } else {
                consecutiveFree = 0;
            }
        }

        return OptionalInt.empty();
    }

    public boolean canAccess(Process process, int pageNumber) {
        Process owner = frametoProcessMap.get(pageNumber);
        return owner != null && owner.equals(process);
    }

    public List<Process> getFrameAllocationList() {
        return Collections.unmodifiableList(frameAllocationList);
    }

    public boolean isFrameFree(int frame) {
        return frameAllocationList.get(frame) == null;
    }

    public Process getFrameOwnerProcess(int frame) {
        return frameAllocationList.get(frame);
    }
}