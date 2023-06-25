package Processor;

public class NOP extends Instruction{

    public NOP(String mnemonic, int size, boolean privileged, int numOfOperands) {
        super(mnemonic, size, privileged, numOfOperands);
        //TODO Auto-generated constructor stub
    }
    @Override
    public void execute() {
        System.out.println("Doin Nothing");
    }
    
}
