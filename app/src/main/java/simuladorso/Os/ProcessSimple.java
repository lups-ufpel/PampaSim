package simuladorso.Os;

public class ProcessSimple extends ProcessAbstract {

    public ProcessSimple(int pid) {
        super(pid);
    }
    public void setState(Process.State state){
        this.state = state;
    }
    @Override
    public boolean hasInterrupt() {
        return false;
    }
    @Override
    public void setLength(int length){
        this.length = 100;
    }
}
