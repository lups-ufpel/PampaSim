package org.simuladorso.Os.MemoryAllocationAlgorithms;

import java.util.ArrayList;

import org.simuladorso.VirtualMachine.Sbyte;
import org.simuladorso.VirtualMachine.Memory;

public interface MemoryAllocator {
	ArrayList<Sbyte> allocateMemory(Memory memory, boolean memoryMap[], int size);
}
