package Kernel;

public class Interruption {
    private InterruptionTable interruptionTable;

    public Interruption(){
        this.interruptionTable = InterruptionTable.NONE;
    }
    
    public InterruptionTable get() {
        return interruptionTable;
    }
    public void set(InterruptionTable interruptionTable) {
        this.interruptionTable = interruptionTable;
    }
}
