package org.simuladorso.Os.MemoryAllocationAlgorithms;

import java.util.ArrayList;

<<<<<<< HEAD:src/Os/MemoryAllocationAlgorithms/MemoryAllocator.java
import simuladorso.Vm.Memory;
import simuladorso.Vm.Sbyte;
=======
import org.simuladorso.VirtualMachine.Sbyte;
import org.simuladorso.VirtualMachine.Memory;
>>>>>>> jansoares:src/main/java/org/simuladorso/Os/MemoryAllocationAlgorithms/MemoryAllocator.java

public interface MemoryAllocator {
	ArrayList<Sbyte> allocateMemory(Memory memory, boolean memoryMap[], int size);
}
