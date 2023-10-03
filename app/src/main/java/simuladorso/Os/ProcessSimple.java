package simuladorso.Os;

public class ProcessSimple extends Process {

    public ProcessSimple(int pid) {
        super(pid);
    }
    public void setState(Process.State state){
        this.state = state;
    }
    @Override
    public void setLength(int length){
        this.length = 100;
    }
}
