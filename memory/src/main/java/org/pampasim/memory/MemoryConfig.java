package org.pampasim.memory;

import lombok.Getter;
import lombok.Setter;

public final class MemoryConfig {

    @Getter @Setter private static int pageSize;
    @Getter @Setter private static int maxPagesPerProcess;
    @Getter @Setter private static int framesInRAM;
    @Getter @Setter private static int framesInSwap;

    private MemoryConfig() {
        throw new AssertionError("Cannot instantiate static configuration class");
    }

    public static void initialize(int pageSize, int maxPages, int ramFrames, int swapFrames) {
        validatePowerOfTwo("Page size", pageSize);
        validatePowerOfTwo("Max pages per process", maxPages);

        setPageSize(pageSize);
        setMaxPagesPerProcess(maxPages);
        setFramesInRAM(ramFrames);
        setFramesInSwap(swapFrames);
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

    public static long extractOffsetNumber(long virtualAddress) {
        int pageOffsetBits = getPageOffsetBits();
        long offsetMask = (1L << pageOffsetBits) - 1;
        return virtualAddress & offsetMask;
    }
}
