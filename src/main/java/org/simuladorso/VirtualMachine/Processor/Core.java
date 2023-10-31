package org.simuladorso.VirtualMachine.Processor;

import org.simuladorso.Os.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Core {

    Logger LOGGER = LoggerFactory.getLogger(Core.class.getSimpleName());

    void execute(Process process);
}
