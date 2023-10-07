import java.util.ArrayList;

// import Mediator.Mediator;
// import Mediator.MediatorDefault;
// import VirtualMachine.*;
// import VirtualMachine.Processor.CoreLuan;
// import Os.MemoryManager;
// import Os.Os;
// import Os.Scheduler;
// import Os.SchedulerLuan;
// import Os.Process;

public class Main {

    // static int i =0;
    // static ArrayList<Sbyte> memory;
    // static final Process.Type proc_type = Process.Type.COMPLETE;
    // public static void main(String[] args){

    //     final int numCores = 2;
        
    //     // Criando os componentes básicos para o cenário de simulação
    //     Mediator mediator = new MediatorDefault();
    //     Os kernel = new Os();
    //     Scheduler scheduler = new SchedulerLuan(kernel.getNewList(), 
    //                                             kernel.getReadyList(), 
    //                                             kernel.getWaitingList(), 
    //                                             kernel.getTerminatedList(),
    //                                             numCores, mediator);
    //     MemoryManager mem_manager = new MemoryManager(100);
    //     mediator.registerComponent(Mediator.Component.OS, kernel);
    //     mediator.registerComponent(Mediator.Component.SCHEDULER, scheduler);
    //     mediator.registerComponent(Mediator.Component.MEM_MANAGER, mem_manager);
        
    //     mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type});
    //     Process process = (Process) mediator.invoke(Mediator.Action.GET_PROCESS_BY_PID, new Object[]{0});
    //     memory = process.getMemory(); // get the memory block of the process

    //     // Insert a program
    //     /**
    //      * ADDI 0, 0, 15
    //      * ADDI 1, 1, 20
    //      * ADD 3, 0, 1
    //      * 
    //      * ADDI 2, 2, 10 (V0 = 10)
    //      * SYSCALL (exit)
    //      */

    //     String op = "001000"; // ADDI
    //     String rs = "01000"; // 8 (t0)
    //     String rt = "01000"; // 8 (t0)
    //     String Imm = "0000000000001111"; // 15

    //     String instruction = op + rs + rt + Imm;
    //     System.out.println(instruction);

    //     insertInstruction(instruction);

    //     op = "001000"; // ADDI
    //     rs = "01001"; // 9 (t1)
    //     rt = "01001"; // 9 (t1)
    //     Imm = "0000000000010100"; // 20

    //     instruction = op + rs + rt + Imm;
    //     System.out.println(instruction);

    //     insertInstruction(instruction);

    //     op = "000000"; // R-Format
    //     rs = "01000"; // 8 (t0)
    //     rt = "01001"; // 9 (t1)
    //     String rd = "01010"; // 10 (t3)
    //     String shamt = "00000"; // 0
    //     String funct = "100000"; // ADD

    //     instruction = op + rs + rt + rd + shamt + funct;
    //     System.out.println(instruction);
        
    //     insertInstruction(instruction);

    //     // $v0 = 10 (exit)
    //     op = "001000"; // ADDI
    //     rs = "00010"; // 2 (v0)
    //     rt = "00010"; // 2 (v0)
    //     Imm = "0000000000001010"; // 10

    //     instruction = op + rs + rt + Imm;
    //     System.out.println(instruction);

    //     insertInstruction(instruction);

    //     op = "011010"; // SYSCALL
    //     rs = "00000";
    //     rt = "00000";
    //     Imm = "0000000000000000";

    //     instruction = op + rs + rt + Imm;
    //     System.out.println(instruction);

    //     insertInstruction(instruction);

    //     for (int a = 0; a < i; a++) {
    //         System.out.print(memory.get(a).getStringByte() + " ");
    //         if (a % 4 == 3)
    //             System.out.println();
    //     }
    //     Vm <CoreLuan> vm = new VmLuan(numCores, mediator);
    //     mediator.registerComponent(Mediator.Component.VM, vm);
    //     vm.run();
    // }

    // public static void insertInstruction(String instruction) {
    //     for (int a = 0; a < 4; i++, a++) {
    //         System.out.println(i);
    //         memory.get(i).setByte(instruction.substring(8 * a, 8 * a + 8));
    //     }
    // }
}
