package org.pampasim.VirtualMachine.Processor;

import org.pampasim.Os.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An interface to be implemented by each class that provides
 * the basic features of virtual processor Core of a Machine.
 * Each Core is responsible for executing through {@link #execute(Process)},
 * of one or more instructions within a process/tasks.
 *
 * <p>
 * Usage Example:
 * <pre>
 * Core core = new CoreImpl();
 * core.execute(someProcess);
 * </pre>
 *
 *
 * @version 1.0
 * @since 2023-3-21
 */
public interface Core {


    /**
     * A logger for recording log messages related to the Core functionality.
     */
    Logger LOGGER = LoggerFactory.getLogger(Core.class.getSimpleName());

    /**
     * Executes the process provided, advancing to its conclusion in one or more instructions.
     * @param process The process to be executed.
     */
    void execute(Process process);
}
