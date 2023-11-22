package org.simuladorso.VirtualMachine;

import org.simuladorso.Utils.SimulatorClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An interface to be implemented by each class that represents a Virtual Machine (VMs)
 * in the PampaOS simulator. It provides mechanisms for running, starting, and stopping a VM, as well as a Logger for logging
 * VM-related activities and a SimulatorClock for tracking time within the VM.
 *
 *
 * @version 1.0
 * @since 2023-3-21
 */

public interface Vm {

    /**
     * A logger for recording log messages related to the VM.
     */
    Logger LOGGER = LoggerFactory.getLogger(Vm.class.getSimpleName());

    /**
     * A simulator clock for tracking time within the VM.
     */
    SimulatorClock SIM_CLOCK = new SimulatorClock();

    /**
     * Run the virtual machine, executing the simulation of an OS.
     */
    void run();

    /**
     * Start the virtual machine.
     *
     * @return true if the VM can be started
     */
    boolean start();

    /**
     * Stop the virtual machine.
     *
     * @return true if the VM execution can be finished
     */
    boolean stop();
}
