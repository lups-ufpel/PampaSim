package org.pampasim.Os;

import org.pampasim.Mediator.Mediator;
import org.pampasim.gui.model.ProcessMock;

public class MockOs extends Os{

    /**
     * Constructs an `Os` instance with the specified mediator for communication.
     *
     * @param mediator The mediator for communication.
     */
    public MockOs(Mediator mediator) {
        super(mediator);
    }
    @Override
    public ProcessMock createProcess(Process.Type type, int burst, int priority, int arrivalInstant) {
        PidAllocator.Pid pid = getPidAllocator().AssignPid();
        ProcessMock newProcess = new ProcessMock(pid, priority,burst,arrivalInstant);
        System.out.println("Created process state " + newProcess.getState());
        processList.add(newProcess);
        return newProcess;
    }
}
