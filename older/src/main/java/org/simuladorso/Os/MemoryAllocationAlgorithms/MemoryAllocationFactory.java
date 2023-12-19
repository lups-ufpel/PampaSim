package org.simuladorso.Os.MemoryAllocationAlgorithms;

import org.simuladorso.Os.MemoryAllocationStrategy;

public class MemoryAllocationFactory {
	public static MemoryAllocator getMemoryAllocator(MemoryAllocationStrategy strategy){
		switch(strategy){
			case FIRST_FIT:
				return new FirstFitMemoryAllocator();
			default:
				return null;
		}
	}
}
