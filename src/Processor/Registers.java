package Processor;
public final class Registers {

    private static final int NUM_REGISTERS = 8;
    
    private Register PC; // PROGRAM COUNTER
    private Register SP; // STACK POINTER
    private Register IE; //INTERRUPTION ENABLE
    
    private Register[] GPR; // General Purpose Registers

    public Registers(){

        this.PC = new Register();
        this.SP = new Register();
        this.IE = new Register();
        this.GPR = new Register[NUM_REGISTERS];
    }

    public static int countRegisters(){
        return NUM_REGISTERS;
    }
    public int getProgramCounter(){
        return this.PC.getValue();
    }
    public int getStackPointer(){
        return this.SP.getValue();
    }
    public int getInterruption(){
        return this.IE.getValue();
    }
    public int getGeneralPurposeReg(int index){
        if(index >= 0 && index <= NUM_REGISTERS){
            return GPR[index].getValue();
        }
        else {
            throw new IllegalArgumentException("Invalid register index: " + index);
        }
    }
    @Override
    public String toString() {
        return "Register{" + 
                    " " + 
                    " ";
    }
} 
