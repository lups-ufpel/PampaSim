package simuladorso;

public class Main {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        Cpu cpu = new Cpu();
        Kernel kernel = Kernel.getInstance();
        
        //Creating 3 process for testing
        kernel.fork();
        kernel.fork();
        kernel.fork();

        while(true){    
            PCB process = kernel.scheduler.executeProcess();

            System.out.println("Processo PID: " + process.getPid() + " pronto para executar");
            
            for (int i = 1; i <= kernel.getTimeSlice(); i++) {
                cpu.cycle(process);
            }

            kernel.scheduler.preemptProcess();
            
            if(kernel.scheduler.verifyAllQueues())
                break;
        }
        System.out.println("Finalizando Execução do Simulador.");
    }
}