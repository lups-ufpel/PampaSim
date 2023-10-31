package org.simuladorso.VirtualMachine;

import org.simuladorso.Os.Interruption;
import org.simuladorso.Os.Process;
import org.simuladorso.VirtualMachine.Processor.Core;
import org.simuladorso.VirtualMachine.Processor.CoreSimple;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.VirtualMachine.Processor.Registers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class VmSimple implements Vm {

    public Interruption interruption;
    protected final int numCores;
    protected final Core[] cores;
    protected Mediator mediator;

    public VmSimple(int numCores, Mediator mediator) {
        if(mediator == null || numCores <= 0){
            throw new IllegalArgumentException("Mediator cannot be null and numCores must be greater than 0");
        }
        this.numCores = numCores;
        this.mediator = mediator;
        cores = new Core[numCores];
        for(int i=0; i < numCores; i++){
            cores[i] = new CoreSimple();
        }

    }
    public List<Process> getRunningProcesses() {
        List<Process> processes;
        try {
            Object result = mediator.invoke(Mediator.Action.SCHEDULER_SCHEDULE);
            processes = (List<Process>) result;
            LOGGER.debug("List of Processes to be Scheduled: {}", Arrays.toString(processes.toArray()));
            return processes;
        } catch (Exception e) {
            // Handle the error, log it, or return an empty list
            LOGGER.error("Error in method getRunningProcesses: {}", e.getMessage() );
            throw new RuntimeException();
        }
    }


    public void executeProcesses(List<Process> processes){
        Process proc;
        for(int coreId =0; coreId < numCores; coreId++){
            proc = processes.get(coreId);
            if(proc != null){
                mediator.invoke(Mediator.Action.CORE_EXECUTE, new Object[]{proc, cores[coreId]});
                if (proc.hasInterrupt()) {
                    LOGGER.debug("Process of PID {} has requested an interruption",proc.getPid());
                    interruptionHandler(proc);
                }
            }
            else{
                LOGGER.info("Core [{}] is idle",coreId);
            }
        }
    }

    @Override
    public void run() {
        List<Process> runningProcesses;
        while(!Thread.currentThread().isInterrupted()) {

            SIM_CLOCK.incrTick();

            LOGGER.debug("Current Tick: [{}]",SIM_CLOCK.getTick());

            runningProcesses = getRunningProcesses();

            executeProcesses(runningProcesses);

            System.out.println("====================================");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(stop()){
                break;
            }
        }
    }

    @Override
    public boolean start() {
        // THIS IS A PLACEHOLDER IMPLEMENTATION
        LOGGER.info("VM started successfully");
        return true;
    }

    public boolean stop(){
        return (boolean) mediator.invoke(Mediator.Action.GET_SIM_STATUS);
    }

    public void interruptionHandler(Process process) {

        interruption = process.getInterruption();

        int pid = process.getPid();
        Registers reg = process.getRegisters();
        Registers registers = process.getRegisters();
        List<Sbyte> memory = process.getMemory();

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
