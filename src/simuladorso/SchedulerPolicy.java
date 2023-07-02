package simuladorso;

public interface SchedulerPolicy {
    public Process getProcess();
    public void addProcess(Process process);
}
