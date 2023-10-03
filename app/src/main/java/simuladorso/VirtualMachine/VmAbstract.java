package VirtualMachine;
import VirtualMachine.Processor.Core;
import Kernel.Process;
public abstract class VmAbstract <T extends Core> {
    
    protected T[] cores;
    protected Process[] runningList;

    public VmAbstract(T[] corelist){
        this.cores = corelist;
        System.out.println("Virtual Machine created with " +  getNumCores() + " cores");
    }
    public abstract void run();
    protected void interruptionHandler(Process process){
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }
    
    protected void interruptionHandler(){
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }

    public int getNumCores(){
        return cores.length;
    }
}
