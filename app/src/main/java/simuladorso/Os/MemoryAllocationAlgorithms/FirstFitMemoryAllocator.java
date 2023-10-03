package simuladorso.Os.MemoryAllocationAlgorithms;

import java.util.ArrayList;

import simuladorso.Vm.Memory;
import simuladorso.Vm.Sbyte;

public class FirstFitMemoryAllocator implements MemoryAllocator {
	private static final int MAP_SIZE = 16;

	@Override
	public ArrayList<Sbyte> allocateMemory(Memory memory, boolean memoryMap[], int size){
		ArrayList<Sbyte> allocatedMemory;

		int start = 0;
		int end = 0;
		int count = 0;

		for (int i = 0; i < memoryMap.length; i++) {

			// If the count is equal to the size, then it'll be the end of the stack
			// and break the loop, because we already found the needed space
			if (count == size) {
				end = (i * MAP_SIZE);
				break;
			}

			// Check if the memory is free
			if (memoryMap[i] == false) {
				// If the count is 0, then it'll be the start of the stack
				if (count == 0) {
					start = i * MAP_SIZE;
				}
				count += MAP_SIZE;
			} else {
				// If the memory is not free, then reset the count
				count = 0;
			}
		}

		// If the count is less than the size, then there's no enough space
		if (count < size) {
			return null;
		}

		allocatedMemory = memory.getMemBlock(start, end);

		//Mark the memory as allocated
		for (int i = start / MAP_SIZE; i < end / MAP_SIZE; i++) {
			memoryMap[i] = true;
		}

		return allocatedMemory;
	}
	
}
