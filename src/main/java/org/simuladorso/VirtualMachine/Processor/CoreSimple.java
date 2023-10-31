package org.simuladorso.VirtualMachine.Processor;

import org.simuladorso.Os.Process;


public class CoreSimple implements Core {

    @Override
    public void execute(Process process) {

        process.forwardProcessExecution();
        int ticks_executed = process.getCurrentExecutionTime();
        int total_ticks = process.getBurstTime();
        LOGGER.info("Process of PID {} executed {} instr out of {}.",process.getPid(),ticks_executed, total_ticks);
        if(ticks_executed >= total_ticks){
            process.setState(Process.State.TERMINATED);
            LOGGER.info("Process of PID {} has terminated",process.getPid());
        }
    }
}
