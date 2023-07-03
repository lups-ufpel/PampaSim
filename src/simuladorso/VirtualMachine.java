package simuladorso;

public class VirtualMachine {

    // atributos provisórios até implementar I/O com usuário
    private static final int TIME_SLICE =3;
    private static final int NUM_PROCESSES = 3;
    
    long startTime;
    long endTime;
    //----------------------------
    Processor processor;
    Kernel kernel;
    RoundRobinScheduler scheduler;
    private void initialize(){
        processor = Processor.getInstance();
        kernel = Kernel.getInstance();
        scheduler = new RoundRobinScheduler(TIME_SLICE);
        createFixedNumberProcesses();
    }
    private void runSimulation(){

        Process processPlaceHolder;
        while(true) {
            processPlaceHolder = kernel.scheduleProcess(scheduler);
            System.out.println("Processo PID: " + processPlaceHolder.getPid() + " pronto para executar");
            
            //Um processo executa uma quantidade fixa de ciclos de cpu
            //baseado na fatia de tempo(quantum) alocada pelo escalonador
            for (int i = 1,quantum = scheduler.getTimeSlice(); i <= quantum; i++) {
                       if(!processor.cycle(processPlaceHolder))break;
                    }

            kernel.preemptProcess(scheduler);
            
            //Se todos os procesos encontram-se no estado TERMINADO
            // a simulação é encerrada
            if (scheduler.verifyAllQueues())break;
         }
    }
    private void cleanup(){
        endTime = System.currentTimeMillis();
        System.out.println("Finalizando Execução do Simulador.");
        double duration = (endTime - startTime) / 1000; // Duração em segundos
        System.out.println("Tempo de execução: " + duration + " segundos");
    }
    private void createFixedNumberProcesses(){
        for(int i=0;i<NUM_PROCESSES;i++){
            kernel.fork(scheduler);
        }
    }

    public static void main(String[] args) {
        VirtualMachine machine = new VirtualMachine();
        machine.initialize();
        machine.startTime = System.currentTimeMillis();
        machine.runSimulation();
        machine.cleanup();
    }
}