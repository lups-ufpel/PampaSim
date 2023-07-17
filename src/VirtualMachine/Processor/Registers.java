package VirtualMachine.Processor;

public class Registers {
    private static final int REG_AMOUNT = 32;

    private int registers[];

    public Registers() {
        registers = new int[REG_AMOUNT];
    }

    public int getReg(int index) {
        return registers[index];
    }

    public void setReg(int index, int value) {
        registers[index] = value;
    }
}
