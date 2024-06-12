package org.pampasim.Os.MemoryAllocationAlgorithms;

import org.pampasim.Os.MemoryAllocationStrategy;

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