package Processor;

public class CPU {

    private Registers registers;

    public static final String[] Instuctions = {
        "NOP",
        "HALT",
    };
    public CPU(){

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