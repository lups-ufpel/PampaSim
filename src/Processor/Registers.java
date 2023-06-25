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
        for (int i = 0; i < NUM_REGISTERS; i++) {
            GPR[i] = new Register();
        }
    }

    public static int countRegisters(){
        return NUM_REGISTERS;
    }
    public int getProgramCounter(){
        return PC.getValue();
    }
    public void setProgramCounter(int value){
        PC.setValue(value);
    }
    public int getStackPointer(){
        return SP.getValue();
    }
    public void setStackPointer(int value){
        SP.setValue(value);
    }
    public int getInterruption(){
        return IE.getValue();
    }
    public void setInterruption(int value){
        IE.setValue(value);
    }
    public Register getInterruptionRegister(){
        return IE;
    }
    public Register getGeneralPurposeReg(int index){
        if(index >= 0 && index <= NUM_REGISTERS){
            return this.GPR[index];
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
