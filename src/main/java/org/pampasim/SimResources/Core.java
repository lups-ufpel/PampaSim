package org.pampasim.SimResources;

/**
 * An interface to be implemented by each class that provides
 * the basic features of virtual processor resources.Core of a Machine.
 * Each resources.Core is responsible for executing through {@link #execute(Process)},
 * of one or more instructions within a process/tasks.
 *
 * <p>
 * Usage Example:
 * <pre>
 * resources.Core core = new CoreImpl();
 * core.execute(someProcess);
 * </pre>
 *
 *
 * @version 1.0
 * @since 2023-3-21
 */
public interface Core {
    /**
     * Executes the process provided, advancing to its conclusion in one or more instructions.
     * @param process The process to be executed.
     */
    void execute(Process process);

    enum Status {
        FREE,
        BUSY
    }
}
