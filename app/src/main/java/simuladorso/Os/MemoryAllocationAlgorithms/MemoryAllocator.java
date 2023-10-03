package simuladorso.Os.MemoryAllocationAlgorithms;

import java.util.ArrayList;

import simuladorso.Vm.Memory;
import simuladorso.Vm.Sbyte;

public interface MemoryAllocator {
	ArrayList<Sbyte> allocateMemory(Memory memory, boolean memoryMap[], int size);
}
