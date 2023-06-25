package Processor;

public final class HALT extends Instruction{


    private Register reg;
    public HALT(String mnemonic, int size, boolean privileged, int numOfOperands) {
        super(mnemonic, size, privileged, numOfOperands);
        //TODO Auto-generated constructor stub
    }

    public void setOperand(Register ex){
        reg = ex;
    }
    @Override
    public void execute() {
        reg.setValue(-1);
    }

    
    
}
