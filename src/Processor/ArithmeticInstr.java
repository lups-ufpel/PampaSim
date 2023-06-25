package Processor;

public abstract class ArithmeticInstr extends Instruction{

    protected Register reg1;
    protected Register reg2;
    public ArithmeticInstr(String mnemonic, int size, boolean privileged, int numOfOperands) {
        super(mnemonic, size, privileged, numOfOperands);
        //TODO Auto-generated constructor stub
    }
    public void setFirstOperand(Register op){
        reg1 = op;
    }
    public void setSecondOperand(Register op){
        reg2 = op;
    }

    
}
