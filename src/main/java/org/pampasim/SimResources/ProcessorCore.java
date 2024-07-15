package org.pampasim.SimResources;

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

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public void execute(Process process) {
        process.forwardProcessExecution();
        System.out.println("[Núcleo CPU] Executando um tick do processo: " +
                process.name + " (tempo restante): " + process.getRemainingExecutionTime());
    }

    public boolean isFree() {
        return Status.FREE.equals(status);
    }
}
