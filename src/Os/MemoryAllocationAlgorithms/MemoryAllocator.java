package Os.MemoryAllocationAlgorithms;

import java.util.ArrayList;

import VirtualMachine.Memory;
import VirtualMachine.Sbyte;

public interface MemoryAllocator {
	ArrayList<Sbyte> allocateMemory(Memory memory, boolean memoryMap[], int size);
}
