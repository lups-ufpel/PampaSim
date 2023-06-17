package Processor;
public final class Registers {

    private static final int NUM_REGISTERS = 8;
    
    private Register PC; // PROGRAM COUNTER
    private Register SP; // STACK POINTER
    private Register IE; //INTERRUPTION ENABLE
    
    private Register[] GPR; // General Purpose Registers

    public Registers(){

        PC = new Register();
        SP = new Register();
        IE = new Register();
        GPR = new Register[NUM_REGISTERS];
    }

    public static int countRegisters(){
        return NUM_REGISTERS;
    }
    public int getProgramCounter(){
        return PC.getValue();
    }
    public int getStackPointer(){
        return SP.getValue();
    }
    public int getInterruption(){
        return IE.getValue();
    }
    public int getGeneralPurposeReg(int index){
        if(index >= 0 && index <= NUM_REGISTERS){
            return this.GPR[index].getValue();
        }
        else {
            throw new IllegalArgumentException("Invalid register index: " + index);
        }
    }
    public void setGeneralPurposeReg(int index, int value) throws Exception{
        if(index >= 0 && index <= NUM_REGISTERS) {
           this.GPR[index].setValue(value);        
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
