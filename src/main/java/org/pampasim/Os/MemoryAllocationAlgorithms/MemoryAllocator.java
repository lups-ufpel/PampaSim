package org.pampasim.Os.MemoryAllocationAlgorithms;

import java.util.ArrayList;

import org.pampasim.VirtualMachine.Sbyte;
import org.pampasim.VirtualMachine.Memory;

public interface MemoryAllocator {
	ArrayList<Sbyte> allocateMemory(Memory memory, boolean memoryMap[], int size);
}
