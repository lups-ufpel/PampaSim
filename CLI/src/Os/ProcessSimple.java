package Os;

public class ProcessSimple extends Process{

    public ProcessSimple(int pid) {
        super(pid);
    }
    public ProcessSimple(int pid, int priority) {
        super(pid, priority);
    }
    public ProcessSimple(int pid, int length, int priority) {
        super(pid, priority, length);
    }
    public void setState(Process.State state){
        this.state = state;
    }
    public boolean hasInterrupt() {
        return false;
    }
}
