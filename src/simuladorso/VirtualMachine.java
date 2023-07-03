package simuladorso;

public class VirtualMachine {
    public static void main(String[] args) {
        Processor processor = Processor.getInstance();
        RoundRobinScheduler sched = new RoundRobinScheduler(2);
        Kernel kernel = Kernel.getInstance();
        Process process;
        //Creating 3 process for testing
        kernel.fork(sched);
        kernel.fork(sched);
        kernel.fork(sched);
        long startTime = System.currentTimeMillis();
        while(true) {
            process = kernel.scheduleProcess(sched);
            System.out.println("Processo PID: " + process.getPid() + " pronto para executar");
            for (int i = 1,quantum = sched.getTimeSlice(); i <= quantum; i++) {
                       if(!processor.cycle(process))break;
                    }

            kernel.preemptProcess(sched);
            
            if (sched.verifyAllQueues())break;
         }
        long endTime = System.currentTimeMillis();
        double duration = (endTime - startTime) / 1000; // Duração em segundos
        System.out.println("Tempo de execução: " + duration + " segundos");
        System.out.println("Finalizando Execução do Simulador.");
    }
}