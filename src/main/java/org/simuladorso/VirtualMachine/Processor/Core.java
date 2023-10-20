package org.simuladorso.VirtualMachine.Processor;

import java.util.ArrayList;
import java.util.HashMap;

import org.simuladorso.Os.Process;
import org.simuladorso.Os.Interruption;
import org.simuladorso.VirtualMachine.Sbyte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Core {

    final Logger LOGGER = LoggerFactory.getLogger(getClass().getSimpleName());
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
