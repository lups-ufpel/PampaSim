package VirtualMachine;
import VirtualMachine.Processor.Core;
import Mediator.Mediator;
import Os.Process;
public abstract class Vm <T extends Core> {
    protected final int numCores;
    protected final T[] cores;
    protected Process[] runningList;
    protected Mediator mediator;

    public Vm(T[] corelist){
        this.cores = corelist;
        this.numCores = cores.length;
        System.out.println("Virtual Machine created with " +  numCores + " cores");
    }
    public abstract void run();
    protected void interruptionHandler(Process process){
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }
    
    protected void interruptionHandler(){
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }

    public int getNumCores(){
        return numCores;
    }
    public void start(){
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }
    public void stop(){
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }
    public Process [] getRunningList(){
        throw new UnsupportedOperationException("Unimplemented method 'getRunningList'");
    }
}
