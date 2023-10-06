package VirtualMachine.Processor;

public class Registers {
    private static final int REG_AMOUNT = 32;

    private Register registers[];

    private Register PC;
    private Register HI;
    private Register LO;

    public Registers() {
        registers = new Register[REG_AMOUNT];
        for (int i = 0; i < REG_AMOUNT; i++) {
            registers[i] = new Register();
        }
        PC = new Register();
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
        switch (reg) {
            case "PC":
                return PC;
            case "RA":
                return registers[31];
            case "HI":
                return HI;
            case "LO":
                return LO;
            case "at":
                return registers[1];
            case "V0":
                return registers[2];
            case "V1":
                return registers[3];
            case "A0":
                return registers[4];
            case "A1":
                return registers[5];
            case "A2":
                return registers[6];
            case "A3":
                return registers[7];
            default:
                // Converts from String to binary
                int regIndex = Integer.parseInt(reg.substring(1), 2);
                return registers[regIndex];
        }
    }

    public void incrementPC() {
        PC.setValue(PC.getValue() + 1);
    }
}