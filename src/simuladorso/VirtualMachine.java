package simuladorso;

public class VirtualMachine {
    public static void main(String[] args) {
        Processor processor = Processor.getInstance();
        Kernel kernel = Kernel.getInstance();
        
        //Creating 3 process for testing
        kernel.fork();
        kernel.fork();
        kernel.fork();
        long startTime = System.currentTimeMillis();
        while(true) {
            Process process = kernel.scheduler.executeProcess();
            System.out.println("Processo PID: " + process.getPid() + " pronto para executar");
            for (int i = 1; i <= kernel.scheduler.getTimeSlice(); i++) {
                if(!processor.cycle(process))break;
            }

            kernel.scheduler.preemptProcess();
            
            if (kernel.scheduler.verifyAllQueues())
                break;
        }
         long endTime = System.currentTimeMillis();//System.nanoTime(); // Tempo final
        double duration = (endTime - startTime) / 1000; // Duração em segundos
        System.out.println("Tempo de execução: " + duration + " segundos");
        System.out.println("Finalizando Execução do Simulador.");
    }
}