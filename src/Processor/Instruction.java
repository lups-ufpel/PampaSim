package Processor;

public abstract class Instruction {
 
    private final String mnemonic;
    private final int size;
    private final boolean privileged;
    private final int numOfOperands;
    public Instruction(String mnemonic, int size, boolean privileged, int numOfOperands){
        this.mnemonic = mnemonic;
        this.size = size;
        this.privileged = privileged;
        this.numOfOperands = numOfOperands;
    }
    public String getInstructionName(){
        return mnemonic;
    }
    public int getNumOfOperands(){
        return numOfOperands;
    }
    
    public abstract void execute();
}
