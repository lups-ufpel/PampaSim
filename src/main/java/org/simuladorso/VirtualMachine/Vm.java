package org.simuladorso.VirtualMachine;

import org.simuladorso.Os.Interruption;
import org.simuladorso.Utils.SimulatorClock;
import org.simuladorso.VirtualMachine.Processor.Core;
import java.util.List;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Vm {

    final Logger LOGGER = LoggerFactory.getLogger(getClass().getSimpleName());
    public final SimulatorClock SIM_CLOCK = new SimulatorClock();
    public Interruption interruption;
    protected final int numCores;
    protected final Core[] cores;
    protected Mediator mediator;

    public Vm(Core[] corelist){
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
    public boolean stop(){
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }
    public Process [] getRunningList(){
        throw new UnsupportedOperationException("Unimplemented method 'getRunningList'");
    }
}
