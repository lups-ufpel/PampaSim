package org.simuladorso.VirtualMachine.Processor;

import org.simuladorso.Os.Process;


public class CoreSimple extends Core{

    public CoreSimple() {
        LOGGER.info("Component {} was created",getClass().getSimpleName());
    }
    @Override
    public void execute(Process process) {

        process.forwardProcessExecution();
        int ticks_executed = process.getCurrentBurst();
        int total_ticks = process.getTotalBurst();
        LOGGER.info("Process of PID {} executed {} instr out of {}.",process.getPid(),ticks_executed, total_ticks);
        if(ticks_executed >= total_ticks){
            process.setState(Process.State.TERMINATED);
            LOGGER.info("Process of PID {} has terminated",process.getPid());
        }
    }
}
