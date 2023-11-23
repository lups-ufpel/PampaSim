package org.simuladorso.VirtualMachine.Processor;

import org.simuladorso.Os.Process;

/**
 * The `CoreSimple` class is a concrete implementation of the {@link Core} interface,
 * providing a simple execution mechanism for processes. It executes a given process,
 * updates its execution progress, and logs relevant information during the execution.
 *
 * <p>
 * Usage Example:
 * <pre>
 * Core core = new CoreSimple();
 * core.execute(someProcess);
 * </pre>
 *
 * @version 1.0
 * @since 2023-10-31
 */
public class CoreSimple implements Core {

    /**
     * {@inheritDoc}
     *
     * Executes the given process, updates its execution progress, and logs relevant information.
     *
     * @param process The process to be executed.
     */
    @Override
    public void execute(Process process) {
        process.forwardProcessExecution();
        int ticks_executed = process.getCurrentExecutionTime();
        int total_ticks = process.getBurstTime();
        LOGGER.info("PID {} => {} [{}/{}].",process.getPid(),process.getProgressBar(),ticks_executed, total_ticks);
        if(ticks_executed >= total_ticks) {
            process.setState(Process.State.TERMINATED);
            LOGGER.info("Process of PID {} has terminated",process.getPid());
        }
    }
}
