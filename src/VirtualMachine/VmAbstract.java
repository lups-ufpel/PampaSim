package VirtualMachine;
import VirtualMachine.Processor.Core;
import Kernel.Process;
public abstract class VmAbstract {
    
    protected Core cores[];
    protected Process[] runningList;

    public VmAbstract(int numCores){
        this.cores = new Core[numCores];
        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }
        System.out.println("Virtual Machine created with " + numCores + " cores");
    }

    public abstract void run();
    protected void interruptionHandler(Process process){
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }
    protected void interruptionHandler(){
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }
}
