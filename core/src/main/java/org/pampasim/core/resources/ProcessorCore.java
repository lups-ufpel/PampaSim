package org.pampasim.core.resources;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@Setter
public class ProcessorCore extends ResourceManageableAbstract implements Core {
    private final Logger LOGGER = LogManager.getLogger(ProcessorCore.class);

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
        LOGGER.debug("[Núcleo CPU] Executando um tick do processo: {}, tempo restante: {}",
                process.getPid(), process.getRemainingExecutionTime()
        );
    }
}
