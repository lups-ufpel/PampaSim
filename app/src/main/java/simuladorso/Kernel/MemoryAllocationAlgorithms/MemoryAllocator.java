package simuladorso.Kernel.MemoryAllocationAlgorithms;

import java.util.ArrayList;

import simuladorso.VirtualMachine.Memory;
import simuladorso.VirtualMachine.Sbyte;

public interface MemoryAllocator {
	ArrayList<Sbyte> allocateMemory(Memory memory, boolean memoryMap[], int size);
}
