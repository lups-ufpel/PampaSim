package org.pampasim.memory;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.memory.ProcessMemoryInfo;

import java.util.ArrayList;
import java.util.List;

public final class MemoryConfig {

    // todo: these attributes are also defined in spec, but are stored here since multiple entities access them

    @Getter @Setter private static int pageSize                        = 0;
    @Getter @Setter private static int maxPagesPerProcess              = 0;
    @Getter @Setter private static int framesInRAM                     = 0;
    @Getter @Setter private static int framesInSwap                    = 0;

    // todo: attributes after this shouldn't be here, they should be defined purely by spec

    @Getter @Setter private static int swapOperationLength             = 0;
    @Getter @Setter private static int workingSetWindow                = 0;
    @Getter @Setter private static String pageSubstitutionAlgorithm    = "";
    @Getter @Setter private static boolean globalPageSubstitution      = false;
    @Getter @Setter private static boolean anticipatedPageLoading      = false;
    @Getter @Setter private static int prePagingRange                  = 0;
    @Getter @Setter private static boolean variablePageAllocation      = false;
    @Getter @Setter private static double variablePageAllocationTopThreshold    = 0;
    @Getter @Setter private static double variablePageAllocationBottomThreshold = 0;
    @Getter @Setter private static boolean TlbEnabled                  = false;
    @Getter @Setter private static int TlbEntries                      = 0;

    @Getter
    private static final List<ProcessMemoryInfo> processMemoryInfos = new ArrayList<>();

    public static void addProcessMemoryInfo(ProcessMemoryInfo info) {
        processMemoryInfos.add(info);
    }

    public static void clearProcessMemoryInfos() {
        processMemoryInfos.clear();
    }



    private MemoryConfig() {
        throw new AssertionError("Cannot instantiate static configuration class");
    }

    public static void initialize(
            int pageSize,
            int maxPagesPerProcess,
            int framesInRAM,
            int framesInSwap,
            int swapOperationLength,
            int workingSetWindow,
            String pageSubstitutionAlgorithm,
            boolean globalPageSubstitution,
            boolean anticipatedPageLoading,
            int prePagingRange,
            boolean variablePageAllocation,
            double variablePageAllocationTopThreshold,
            double variablePageAllocationBottomThreshold,
            boolean tlbEnabled,
            int tlbEntries
    ) {
        validatePowerOfTwo("Page size", pageSize);
        validatePowerOfTwo("Frames in RAM", framesInRAM);
        validatePowerOfTwo("Frames in Swap", framesInSwap);

        setPageSize(pageSize);
        setMaxPagesPerProcess(maxPagesPerProcess);
        setFramesInRAM(framesInRAM);
        setFramesInSwap(framesInSwap);
        setSwapOperationLength(swapOperationLength);
        setWorkingSetWindow(workingSetWindow);
        setPageSubstitutionAlgorithm(pageSubstitutionAlgorithm);
        setGlobalPageSubstitution(globalPageSubstitution);
        setAnticipatedPageLoading(anticipatedPageLoading);
        setPrePagingRange(prePagingRange);
        setVariablePageAllocation(variablePageAllocation);
        setVariablePageAllocationTopThreshold(variablePageAllocationTopThreshold);
        setVariablePageAllocationBottomThreshold(variablePageAllocationBottomThreshold);
        setTlbEnabled(tlbEnabled);
        setTlbEntries(tlbEntries);
    }



    private static void validatePowerOfTwo(String name, int value) {
        if (value <= 0 || (value & (value - 1)) != 0) {
            throw new IllegalArgumentException(name + " must be a power of two");
        }
    }

    public static int getVirtualMemoryPerProcess() {
        return getMaxPagesPerProcess() * getPageSize();
    }

    public static int getPhysicalRAM() {
        return getFramesInRAM() * getPageSize();
    }

    public static int getSwapSpace() {
        return getFramesInSwap() * getPageSize();
    }

    public static int getPageOffsetBits() {
        return Integer.numberOfTrailingZeros(getPageSize());
    }

    public static int getPageNumberBits() {
        int totalBits = Integer.SIZE - Integer.numberOfLeadingZeros(getVirtualMemoryPerProcess() - 1);
        return totalBits - getPageOffsetBits();
    }

    public static int getTotalVirtualAddressBits() {
        return getPageOffsetBits() + getPageNumberBits();
    }

    public static int extractPageNumber(int virtualAddress) {
        int pageOffsetBits = getPageOffsetBits();
        int pageNumberBits = getPageNumberBits();

        if (pageOffsetBits + pageNumberBits > 32) {
            throw new IllegalStateException("Page offset + page number bits exceeds 32 bits");
        }

        int pageNumberMask = (1 << pageNumberBits) - 1;
        return (virtualAddress >>> pageOffsetBits) & pageNumberMask;
    }

    public static int extractOffsetNumber(int virtualAddress) {
        int pageOffsetBits = getPageOffsetBits();
        int offsetMask = (1 << pageOffsetBits) - 1;
        return virtualAddress & offsetMask;
    }

    public static int combineToAddress(int pageNumber, int offset) {
        int pageOffsetBits = getPageOffsetBits();
        int pageNumberBits = getPageNumberBits();

        // Validate inputs
        if (offset < 0 || offset >= getPageSize()) {
            throw new IllegalArgumentException("Offset must be between 0 and " + (getPageSize() - 1));
        }

        int maxPageNumber = (1 << pageNumberBits) - 1;
        if (pageNumber < 0 || pageNumber > maxPageNumber) {
            throw new IllegalArgumentException(
                    String.format("Page number must be between 0 and %d (max for %d bits)",
                            maxPageNumber, pageNumberBits));
        }

        // Combine by shifting page number and OR'ing with offset
        return (pageNumber << pageOffsetBits) | offset;
    }
    public static int toAddress(int frameNumber) {
        return frameNumber * getPageSize();
    }

    public static int toFrameNumber(int address) {
        return address / getPageSize();
    }
}
