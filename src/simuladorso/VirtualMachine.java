package simuladorso;

public class VirtualMachine {
    public static void main(String[] args) {
        Processor processor = Processor.getInstance();
        Kernel kernel = Kernel.getInstance();
        
        //Creating 3 process for testing
        kernel.fork();
        kernel.fork();
        kernel.fork();

        while(true) {
            Process process = kernel.scheduler.executeProcess();
            System.out.println("Processo PID: " + process.getPid() + " pronto para executar");
            for (int i = 1; i <= kernel.scheduler.getTimeSlice(); i++) {
                processor.cycle(process);
            }

            kernel.scheduler.preemptProcess();
            
            if (kernel.scheduler.verifyAllQueues())
                break;
        }
        
        System.out.println("Finalizando Execução do Simulador.");
    }
}