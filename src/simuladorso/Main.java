<<<<<<<< HEAD:src/simuladorso/Main.java
package simuladorso;

public class Main {

    /**
     * @param args the command line arguments
     */
========

enum ProcessState {
	READY, RUNNING, WAITING, TERMINATED
}

public class VirtualMachine {
>>>>>>>> os:src/VirtualMachine.java

    public static void main(String[] args) {
        Cpu cpu = new Cpu();
        Kernel kernel = Kernel.getInstance();
        
        //Creating 3 process for testing
        kernel.fork();
        kernel.fork();
        kernel.fork();

        while(true){
            Process process = kernel.scheduler.executeProcess();
            System.out.println("Processo PID: "+process.getPid()+" pronto para executar");
            for(int i=1; i<=Kernel.timeSlice;i++){
                cpu.cycle(process);
            }

            kernel.scheduler.preemptProcess();
            
            if(kernel.scheduler.verifyAllQueues())
                break;
        }
        System.out.println("Finalizando Execução do Simulador.");
    }
}