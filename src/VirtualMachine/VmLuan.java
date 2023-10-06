package VirtualMachine;


import java.util.ArrayList;
import java.util.Scanner;

import Mediator.Mediator;
import Os.Interruption;
import Os.Process;
import VirtualMachine.Processor.CoreLuan;
import VirtualMachine.Processor.Registers;

public class VmLuan extends Vm<CoreLuan> {
    
    public Interruption interruption;
    public VmLuan(int numCores, Mediator mediator) {
        super(createCores(numCores));
        this.mediator = mediator;
    }

    private static CoreLuan[] createCores(int numCores){
        CoreLuan[] cores = new CoreLuan[numCores];
        for(int i=0; i < numCores; i++){
            cores[i] = new CoreLuan();
        }
        return cores;
    }
    

    @Override
    public void run() {
        
        while(true){
            runningList = (Os.Process[]) mediator.invoke(Mediator.Action.SCHEDULER_SCHEDULE);
            for(int i = 0; i < cores.length; i++){
                if(runningList[i] != null){
                    System.out.print("Core " + i + ": ");
                    //System.out.println("Running process " + runningList[i].getPid() + " on core " + i);
                    //Invoker.invoke("Core", new Message("execute", runningList[i],cores[i]));
                    mediator.invoke(Mediator.Action.CORE_EXECUTE, new Object[]{runningList[i], cores[i]});
                    if (runningList[i].hasInterrupt()) {
                        interruptionHandler(runningList[i]);
                    }
                } 
                else {
                    System.out.println("Core " + i + " has nothing to execute");
                }
            }
            System.out.println("====================================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // percorra todos os processos em runningList se todos existiverem em stado Terminated retorne true
            boolean allTerminated = true;
            for (int i = 0; i < runningList.length; i++) {
                if (runningList[i] != null) {
                    if (runningList[i].getState() != Process.State.TERMINATED) {
                        allTerminated = false;
                        break;
                    }
                }
            }
            if (allTerminated) {
                System.out.println("All processes terminated");
                return;
            }
        }
    }

    @Override
    public void interruptionHandler(Process process) {
        // Interruption interrupt = (Interruption) Invoker.invoke("Process",
        //         new Message("getInterruption", null, process));
        interruption = process.getInterruption();

        //InterruptionTable interruption = interrupt.get();
        int pid = (int) mediator.invoke(Mediator.Action.PROCESS_GET_PID, new Object[]{process});

        Registers registers = process.getRegisters();
        ArrayList<Sbyte> memory = process.getMemory();

        System.out.println("------------------------------------");
        System.out.println("Process " + pid + " -> interruption: " + interruption.get());
        System.out.println("------------------------------------");

        switch (interruption.get()) {
            case PRINT_INT:
                System.out.println("Process " + pid + " -> print: " + registers.getReg("A0").getValue());
                break;
            case PRINT_STR:
                for (int i = registers.getReg("A0").getValue(); memory.get(i).getByte() != 0; i++) {
                    System.out.print((char) memory.get(i).getByte());
                }
                break;
            case READ_INT:
                System.out.println("||SYSCALL|| READ_INT");

                Scanner scanner = new Scanner(System.in);
                int input = scanner.nextInt();
                registers.getReg("V0").setValue(input);

                System.out.println("V0 = " + registers.getReg("V0").getValue());
                break;
            case READ_STR:
                System.out.println("||SYSCALL|| READ_STR");

                scanner = new Scanner(System.in);
                String inputStr = scanner.nextLine();

                int i = registers.getReg("A0").getValue();
                for (int j = 0; j < inputStr.length(); j++, i++) {
                    memory.get(i).setByte((byte) inputStr.charAt(j));
                }
                memory.get(i).setByte((byte) 0);
                break;
            case ALLOC_MEM:
                System.out.println("||SYSCALL|| ALLOC_MEM");

                //int size = registers.getReg("A0").getValue();
                //int address = (int) Invoker.invoke("MemoryManager", new Message("allocMemory", size));
                //registers.getReg("V0").setValue(address);
                break;
            case FREE_MEM:
                System.out.println("||SYSCALL|| FREE_MEM");

                //address = registers.getReg("A0").getValue();
                //size = registers.getReg("A1").getValue();
                //Invoker.invoke("MemoryManager", new Message("freeMemory", address, size));
                break;
            case EXIT:
                process.setState(Process.State.TERMINATED);
                break;
            case PRINT_CHAR:
                System.out.println("||SYSCALL|| PRINT_CHAR");
                System.out.println((char) registers.getReg("A0").getValue());
                break;
            case READ_CHAR:
                System.out.println("||SYSCALL|| READ_CHAR");
                scanner = new Scanner(System.in);
                char inputChar = scanner.next().charAt(0);
                registers.getReg("V0").setValue((int) inputChar);
                break;
            case OPEN_FILE:
                System.out.println("||SYSCALL|| OPEN_FILE");
                String fileName = "";
                for (i = registers.getReg("A0").getValue(); memory.get(i).getByte() != 0; i++) {
                    fileName += (char) memory.get(i).getByte();
                }
                System.out.println("File name: " + fileName);
                System.out.println("--Interrupção não implementada--");
                break;
            case READ_FILE:
                System.out.println("||SYSCALL|| READ_FILE");
                System.out.println("--Interrupção não implementada--");
                break;
            case WRITE_FILE:
                System.out.println("||SYSCALL|| WRITE_FILE");
                System.out.println("--Interrupção não implementada--");
                break;
            case CLOSE_FILE:
                System.out.println("||SYSCALL|| CLOSE_FILE");
                System.out.println("--Interrupção não implementada--");
                break;
            default:
                break;
        }
    }
}
