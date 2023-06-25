package Processor;

import Processor.Memory.*;
public class VirtualMachine {
    
    public static void main(String[] args) throws Exception {
        
        Memory memory = new Memory();
        //TESTING PROGRAM
        memory.writeWord(0, "ADD");
        memory.writeWord(1, "R0");
        memory.writeWord(2, "R5");
        memory.writeWord(3, "NOP");
        memory.writeWord(4, "NOP");
        //memory.writeWord(0, "ADD");
        //memory.writeWord(1, "R0");
        //memory.writeWord(2, "R0");
        memory.writeWord(5, "HALT");
        CPU singleCoreCPU = new CPU(memory);
        singleCoreCPU.startRegisterFive(5);
        singleCoreCPU.run();
    }
}
