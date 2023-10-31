package org.simuladorso.VirtualMachine;

import org.simuladorso.Utils.SimulatorClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Vm {

    Logger LOGGER = LoggerFactory.getLogger(Vm.class.getSimpleName());
    SimulatorClock SIM_CLOCK = new SimulatorClock();

    void run();

    boolean start();
    boolean stop();
}
