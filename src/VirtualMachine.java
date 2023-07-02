
enum ProcessState {
	READY, RUNNING, WAITING, TERMINATED
}

public class VirtualMachine {
    
    public static void main(String[] args) {
        Cpu cpu = new Cpu();
        Kernel kernel = new Kernel();
        //Creating 3 process for testing
        kernel.fork();
        kernel.fork();
        kernel.fork();
        while(true){
            
            PCB process = kernel.scheduler.executeProcess();
            System.out.println("Processo PID: "+process.getPid()+" pronto para executar");
            for(int i=1; i<=Kernel.timeSlice;i++){
                cpu.cycle(process);
            }
            kernel.scheduler.preemptProcess();
            if(kernel.scheduler.verifyAllQueues())break;
        }
        System.out.println("Finalizando Execução do Simulador.");
    }
}