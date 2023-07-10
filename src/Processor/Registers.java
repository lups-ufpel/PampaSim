package Processor;

public class Registers {
    private Register[] registers;

    public Registers() {
        registers = new Register[32];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = new Register("", 32);
        }
    }

    public Register getReg(int index) {
        return registers[index];
    }

    public void setReg(int index, String value) {
        registers[index].setValue(value);
    }
}
