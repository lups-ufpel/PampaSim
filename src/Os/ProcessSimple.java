package Os;

public class ProcessSimple extends Process{

    public ProcessSimple(int pid) {
        super(pid);
    }
    public ProcessSimple(int pid, int length) {
        super(pid, length);
    }
    public void setState(Process.State state){
        this.state = state;
    }
    public boolean hasInterrupt() {
        return false;
    }
}
