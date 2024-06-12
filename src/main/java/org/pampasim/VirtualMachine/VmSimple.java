package org.pampasim.VirtualMachine;

import org.pampasim.Os.Interruption;
import org.pampasim.Os.Process;
import org.pampasim.VirtualMachine.Processor.Core;
import org.pampasim.VirtualMachine.Processor.CoreSimple;
import org.pampasim.Mediator.Mediator;
import org.pampasim.VirtualMachine.Processor.Registers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The `VmSimple` class is a concrete implementation of the {@link Vm} interface, representing a simple virtual machine (VM) manager.
 * It provides functionality to run, start, and stop the VM, as well as an interruption handler for handling various interruptions
 * during VM execution.
 *
 * <p>
 * Usage Example:
 * <pre>
 * Vm virtualMachine = new VmSimple(numCores, mediator);
 * virtualMachine.start();
 * virtualMachine.run();
 * virtualMachine.stop();
 * </pre>
 *
 * @version 1.0
 * @since 2023-3-21
 */
public class VmSimple implements Vm {

    /**
     * An interruption instance for handling interruptions during VM execution.
     */
    public Interruption interruption;

    /**
     * The number of CPU cores available in the VM.
     */
    protected final int numCores;

    /**
     * An array of CPU cores for executing processes.
     */
    protected final Core[] cores;

    /**
     * A mediator for communication with external components.
     */
    protected Mediator mediator;

    public static List<Process> runningProcesses;

    /**
     * Constructs a `VmSimple` instance with the specified number of CPU cores and a mediator for communication.
     *
     * @param numCores  The number of CPU cores in the VM.
     * @param mediator  The mediator for communication.
     * @throws IllegalArgumentException if the mediator is null or the number of cores is less than or equal to 0.
     */
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

    /**
     * Get a list of running processes from the mediator.
     *
     * @return A list of running processes.
     * @throws RuntimeException if an error occurs during the retrieval of running processes.
     */
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

    /**
     * Execute a list of processes on the available CPU cores.
     *
     * @param processes The list of processes to execute.
     */
    public void executeProcesses(List<Process> processes) {
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

    /**
     * {@inheritDoc}
     *
     * Runs the virtual machine, executing its main operations, handling interruptions, and advancing the simulator clock.
     */
    @Override
    public void run() {
        SIM_CLOCK.incrTick();
        LOGGER.debug("Current Tick: [{}]", SIM_CLOCK.getTick());
        runningProcesses = getRunningProcesses();
        executeProcesses(runningProcesses);
        stop();
    }

    /**
     * {@inheritDoc}
     *
     * Placeholder implementation to start the VM. Logs a message and returns true.
     *
     * @return true to indicate a successful start of the VM.
     */
    @Override
    public boolean start() {
        // THIS IS A PLACEHOLDER IMPLEMENTATION
        LOGGER.info("VM started successfully");
        return true;
    }

    /**
     * Stop the VM by invoking the mediator to get the simulator status.
     *
     * @return true if the VM should stop; otherwise, false.
     */
    public boolean stop(){
        if((boolean) mediator.invoke(Mediator.Action.GET_SIM_STATUS)){
            //mediator.publish(Mediator.Action.STOP_VM);
        }
        return false;
    }


    /**
     * Handle interruptions during VM execution.
     *
     * @param process The process that raised the interruption.
     */
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
