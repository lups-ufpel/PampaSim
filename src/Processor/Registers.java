package Processor;

import VirtualMachine.Sbyte;

public class Registers {
    private static final int REG_AMOUNT = 32;
    private static final int REG_SIZE = 32;

    private Sbyte registers[];

    public Registers() {
        registers = new Sbyte[REG_AMOUNT];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = new Sbyte("", REG_SIZE);
        }
    }

    public Sbyte getReg(int index) {
        return registers[index];
    }

    public void setReg(int index, String value) {
        registers[index].setValue(value);
    }
}
