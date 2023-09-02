package VirtualMachine;

import java.util.ArrayList;

public class Memory {
    private int capacity;
    private ArrayList<Sbyte> memory;

    public Memory(int capacity) {
        this.capacity = capacity;
        memory = new ArrayList<Sbyte>();
    }

    public ArrayList<Sbyte> getMemBlock(int start, int size) {
        int end = start + size;
        
        if (start < 0 || end > capacity) {
            System.out.println("Invalid memory access");
            return null;
        }

        // if the memory was not allocated yet at that position
        if (memory.size() < end){
            for (int i = memory.size(); i < end; i++) {
                memory.add(new Sbyte());
            }
        }

        ArrayList<Sbyte> memBlock = new ArrayList<Sbyte>();
        for (int i = start; i < end; i++) {
            memBlock.add(memory.get(i));
        }
        return memBlock;
    }

    public void setMemBlock(int start, ArrayList<Sbyte> memBlock) {
        int end = start + memBlock.size();
        
        if (start < 0 || end > capacity) {
            System.out.println("Invalid memory access");
            return;
        }

        for (int i = start; i < end; i++) {
            memory.set(i, memBlock.get(i - start));
        }
    }

    public void freeMemBlock(int start, int size) {
        int end = start + size;
        
        if (start < 0 || end > capacity) {
            System.out.println("Invalid memory access");
            return;
        }

        for (int i = start; i < end; i++) {
            memory.set(i, new Sbyte());
        }
    }
}
