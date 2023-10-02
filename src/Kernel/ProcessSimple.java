package Kernel;

public class ProcessSimple extends ProcessAbstract{

    public ProcessSimple(int pid, int length) {
        super(pid, length);
    }
    public void setState(Process.State state){
        this.state = state;
    }
    @Override
    public boolean hasInterrupt() {
        return false;
    }
}
