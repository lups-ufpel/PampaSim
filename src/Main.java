import java.util.ArrayList;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import VirtualMachine.Sbyte;
import VirtualMachine.VirtualMachine;
import Kernel.Process;

/**
 * Classe de teste para o escalonador
 */

public class Main {

    static int i = 0;
    static ArrayList<Sbyte> memory;

    public static void main(String[] args) {

        for (int i = 0; i < 1; i++) {
            Invoker.invoke("Kernel", new Message("newProcess"));
            System.out.println("Processo " + i + " criado");
        }

        insertProgram();

        new VirtualMachine(3);

        // SchedulerTest schedulerTest = new SchedulerTest();
        // schedulerTest.test();
    }

    public static void insertInstruction(String instruction) {
        for (int a = 0; a < 4; i++, a++) {
            System.out.println(i);
            memory.get(i).setByte(instruction.substring(8 * a, 8 * a + 8));
        }
    }

    public static void insertProgram(){
        Process process = (Process) Invoker.invoke("Kernel", new Message("getProcess", 0));

        memory = process.getMemory(); // get the memory block of the process

        // Insert a program
        /**
         * ADDI 0, 0, 15
         * ADDI 1, 1, 20
         * ADD 3, 0, 1
         * 
         * ADDI 2, 2, 10 (V0 = 10)
         * SYSCALL (exit)
         */

        String op = "001000"; // ADDI
        String rs = "01000"; // 8 (t0)
        String rt = "01000"; // 8 (t0)
        String Imm = "0000000000001111"; // 15

        String instruction = op + rs + rt + Imm;
        System.out.println(instruction);

        insertInstruction(instruction);

        op = "001000"; // ADDI
        rs = "01001"; // 9 (t1)
        rt = "01001"; // 9 (t1)
        Imm = "0000000000010100"; // 20

        instruction = op + rs + rt + Imm;
        System.out.println(instruction);

        insertInstruction(instruction);

        op = "000000"; // R-Format
        rs = "01000"; // 8 (t0)
        rt = "01001"; // 9 (t1)
        String rd = "01010"; // 10 (t3)
        String shamt = "00000"; // 0
        String funct = "100000"; // ADD

        instruction = op + rs + rt + rd + shamt + funct;
        System.out.println(instruction);
        
        insertInstruction(instruction);

        // $v0 = 10 (exit)
        op = "001000"; // ADDI
        rs = "00010"; // 2 (v0)
        rt = "00010"; // 2 (v0)
        Imm = "0000000000001010"; // 10

        instruction = op + rs + rt + Imm;
        System.out.println(instruction);

        insertInstruction(instruction);

        op = "011010"; // SYSCALL
        rs = "00000";
        rt = "00000";
        Imm = "0000000000000000";

        instruction = op + rs + rt + Imm;
        System.out.println(instruction);

        insertInstruction(instruction);

        for (int a = 0; a < i; a++) {
            System.out.print(memory.get(a).getStringByte() + " ");
            if (a % 4 == 3)
                System.out.println();
        }
    }
}
