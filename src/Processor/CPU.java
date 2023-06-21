package Processor;

public class CPU {

    
    public static final String[] Instuctions = {
        "NOP",
        "HALT",
    };

    private Registers registers;
    private int programAddress;
    public CPU(){
        
        registers = new Registers();
        
    }

    // Fetches the next instruction from main memory
    private void loadInstruction(){

    }
    private void decode(){

    }
    private void execute(){

    }
    public void run(){

        while (true) {
            loadInstruction();
            this.decode();
            this.execute();

        }
    }

}