import java.util.ArrayList;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import VirtualMachine.Sbyte;
import VirtualMachine.VirtualMachine;
import Kernel.ProcessLuan;

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

        ProcessLuan process = (ProcessLuan) Invoker.invoke("Kernel", new Message("getProcess", 0));

        memory = process.getMemory(); // get the memory block of the process

        // Insert a program
        /**
         * ADDI 0, 0, 15
         * ADDI 1, 1, 20
         * ADD 3, 0, 1
         */

        String op = "001000"; // ADDI
        String rs = "00000"; // 0
        String rt = "00000"; // 0
        String Imm = "0000000000001111"; // 15

        String instruction = op + rs + rt + Imm;
        System.out.println(instruction);

        insertInstruction(instruction);

        op = "001000"; // ADDI
        rs = "00001"; // 1
        rt = "00001"; // 1
        Imm = "0000000000010100"; // 20

        instruction = op + rs + rt + Imm;
        System.out.println(instruction);

        insertInstruction(instruction);

        op = "000000"; // R-Format
        rs = "00000"; // 0
        rt = "00001"; // 1
        String rd = "00011"; // 3
        String shamt = "00000"; // 0
        String funct = "100000"; // ADD

        instruction = op + rs + rt + rd + shamt + funct;
        System.out.println(instruction);

        insertInstruction(instruction);

        for (int a = 0; a <= i; a++) {
            System.out.println(memory.get(a).getStringValue());
        }

        new VirtualMachine(1);

        // SchedulerTest schedulerTest = new SchedulerTest();
        // schedulerTest.test();
    }

    public static void insertInstruction(String instruction) {
        for (int a = 0; a < 4; i++, a++) {
            System.out.println(i);
            memory.get(i).setValue(instruction.substring(8 * a, 8 * a + 8));
        }
    }
}
