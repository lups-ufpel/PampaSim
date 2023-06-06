package VirtualMachine;
import java.util.ArrayList;

public class Processor {

    public static int numOfRegisters = 9;
    public ArrayList<Register> registers = new ArrayList<>();


    public static void main(String[] args) {
        
        System.out.println("hello");

        Processor cpu = new Processor();
        Register reg = new Register("100", true);
        cpu.registers.add(reg);
    }
}