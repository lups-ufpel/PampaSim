package simuladorso.VirtualMachine.Processor;

import java.util.ArrayList;
import java.util.HashMap;

import simuladorso.Os.Process;
import simuladorso.Logger.Logger;
import simuladorso.VirtualMachine.Sbyte;

public abstract class Core {
    ArrayList<Sbyte> memory;

    // HashMap containing the binary instructions and their respective opcode
    protected static HashMap<String, String> Opcodes;
    protected static HashMap<String, String> Funct;

    Registers registers;
    String instruction;
    Register pc;

<<<<<<< HEAD:src/VirtualMachine/Processor/Core.java
    String opcode; // operation code
    Register rs; // source register
    Register rd; // destination register
    Register rt; // target register
    int rsv; // source register value
    int rtv; // target register value
    int shamt; // shift amount
    int imm; // immediate value

    public abstract void execute(Process process);
=======
    public void execute(Process process) {
        if (process == null) {
            Logger.getInstance().debug("No process to be executed");
            return;
        }
>>>>>>> TesteGui:app/src/main/java/simuladorso/VirtualMachine/Processor/Core.java

    public void decodeIFormat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void decodeRFormat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Core() {
    }
}
