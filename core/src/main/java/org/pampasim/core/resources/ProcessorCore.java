package org.pampasim.core.resources;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessorCore extends ResourceManageableAbstract implements Core {

    private static double defaultMips = 1000;
    private Status status;

    public enum Status {
        FREE, BUSY
    }

    public ProcessorCore() {
        this(ProcessorCore.defaultMips);
    }

    public ProcessorCore(final double mipsCapacity) {
        super((long) mipsCapacity, "Unit");
        this.status = Status.FREE;
    }

    @Override
    public void execute(Process process) {
        process.forwardProcessExecution();
        System.out.println("[Núcleo CPU] Executando um tick do processo: " +
                process.getPidString() + " (tempo restante): " + process.getRemainingExecutionTime());
    }
}
