package simuladorso.VirtualMachine.Processor;

public class Registers {
    private static final int REG_AMOUNT = 32;

    private Register registers[];

    private Register PC;
    private Register RA;
    private Register HI;
    private Register LO;

    public Registers() {
        registers = new Register[REG_AMOUNT];
        for (int i = 0; i < REG_AMOUNT; i++) {
            registers[i] = new Register();
        }
        PC = new Register();
        RA = new Register();
        HI = new Register();
        LO = new Register();
    }

    public Register getReg(int index) {
        return registers[index];
    }

    public void setReg(int index, int value) {
        registers[index].setValue(value);
    }

    public Register getReg(String reg) {
        if (reg.equals("PC")) {
            return PC;
        } else if (reg.equals("RA")) {
            return RA;
        } else if (reg.equals("HI")) {
            return HI;
        } else if (reg.equals("LO")) {
            return LO;
        }
        // Converts from String to binary
        int regIndex = Integer.parseInt(reg.substring(1), 2);
        return registers[regIndex];
    }

    public void incrementPC() {
        PC.setValue(PC.getValue() + 1);
    }
}
