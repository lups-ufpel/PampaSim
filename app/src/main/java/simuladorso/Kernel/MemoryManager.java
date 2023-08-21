package simuladorso.Kernel;

import java.util.ArrayList;

import simuladorso.VirtualMachine.Memory;
import simuladorso.VirtualMachine.Sbyte;
import simuladorso.Kernel.MemoryAllocationAlgorithms.MemoryAllocationFactory;
import simuladorso.Kernel.MemoryAllocationAlgorithms.MemoryAllocator;

public class MemoryManager {
	private static final int MAP_SIZE = 16;

    private Memory memory;
    private int mapCapacity;
    private boolean memoryMap[];
	private MemoryAllocator algorithmAllocator;
	private MemoryAllocationStrategy strategy = MemoryAllocationStrategy.FIRST_FIT;

    public MemoryManager(int capacity) {
        this.mapCapacity = capacity / MAP_SIZE;
        memoryMap = new boolean[mapCapacity];

        for (int i = 0; i < mapCapacity; i++) {
            memoryMap[i] = false;
        }

        memory = new Memory(capacity);
    }

    public ArrayList<Sbyte> allocMemory(int size) {
		algorithmAllocator = MemoryAllocationFactory.getMemoryAllocator(strategy);

		return algorithmAllocator.allocateMemory(memory, memoryMap, size);
    }

    public void freeMemory(int start, int size) {
        int end = start + size;

        for (int i = start / MAP_SIZE; i < end / MAP_SIZE; i++) {
            memoryMap[i] = false;
        }

        memory.freeMemBlock(start, size);
    }

	public void setAllocationStrategy(MemoryAllocationStrategy strategy) {
		this.strategy = strategy;
	}

}
