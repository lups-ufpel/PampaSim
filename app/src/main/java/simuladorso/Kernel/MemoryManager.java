package simuladorso.Kernel;

import java.util.ArrayList;

import simuladorso.VirtualMachine.Memory;
import simuladorso.VirtualMachine.Sbyte;

public class MemoryManager {

    private static final int MAP_SIZE = 16;
    private int mapCapacity;
    
    private boolean memoryMap[];
    private Memory memory;

    public MemoryManager(int capacity) {
        this.mapCapacity = capacity / MAP_SIZE;
        memoryMap = new boolean[mapCapacity];

        for (int i = 0; i < mapCapacity; i++) {
            memoryMap[i] = false;
        }

        memory = new Memory(capacity);
    }

    // First Fit algorithm for allocating memory
    public ArrayList<Sbyte> allocMemory(int size) {
        int start = 0;
        int end = 0;
        int count = 0;

        for (int i = 0; i < mapCapacity; i++) {

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

        System.out.println("Allocating memory from " + start + " to " + end);
        // Mark the memory as used
        for (int i = (start / MAP_SIZE); i < (end / MAP_SIZE); i++) {
            memoryMap[i] = true;
        }
        ArrayList<Sbyte> stack = memory.getMemBlock(start, size);

        return stack;
    }

    public void freeMemory(int start, int size) {
        int end = start + size;

        for (int i = start / MAP_SIZE; i < end / MAP_SIZE; i++) {
            memoryMap[i] = false;
        }

        memory.freeMemBlock(start, size);
    }

}
