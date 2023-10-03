package simuladorso.Os;

public class Interruption {
    private InterruptionTable interruptionTable;
    private int targetPid;
    private int targetAddress;

    public Interruption(){
        this.interruptionTable = InterruptionTable.NONE;
    }
    
    public InterruptionTable get() {
        return interruptionTable;
    }
    public void set(InterruptionTable interruptionTable) {
        this.interruptionTable = interruptionTable;
    }

    
    public void setTargetPid(int targetPid) {
        this.targetPid = targetPid;
    }

    public int getTargetPid() {
        return targetPid;
    }

    public void setTargetAddress(int targetAddress) {
        this.targetAddress = targetAddress;
    }

    public int getTargetAddress() {
        return targetAddress;
    }
}
