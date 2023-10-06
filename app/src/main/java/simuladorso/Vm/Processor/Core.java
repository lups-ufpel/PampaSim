package simuladorso.Vm.Processor;

import java.util.ArrayList;
import java.util.HashMap;

import Os.Interruption;
import Os.Process;
import VirtualMachine.Sbyte;

public abstract class Core {
    ArrayList<Sbyte> memory;

    // HashMap containing the binary instructions and their respective opcode
    protected static HashMap<String, String> Opcodes;
    protected static HashMap<String, String> Funct;

    Registers registers;
    String instruction;
    Register pc;
    Interruption interruption;

    String opcode; // operation code
    Register rs; // source register
    Register rd; // destination register
    Register rt; // target register
    int rsv; // source register value
    int rtv; // target register value
    int shamt; // shift amount
    int imm; // immediate value
    public abstract void execute(Process process);

    protected void decodeIFormat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void decodeRFormat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Core() {
    }
}
