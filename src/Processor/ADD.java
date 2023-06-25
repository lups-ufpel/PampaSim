package Processor;

public final class ADD extends ArithmeticInstr{
    public ADD(String mnemonic, int size, boolean privileged, int numOfOperands) {
        super(mnemonic, size, privileged, numOfOperands);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void execute() {

        int soma = reg1.getValue() + reg2.getValue();
        reg1.setValue(soma);
    }
}
