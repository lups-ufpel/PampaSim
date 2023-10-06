package Os;

public class Interruption {
    public enum InterruptionTable {
        NONE,
        PRINT_INT,
        PRINT_STR,
        READ_INT,
        READ_STR,
        ALLOC_MEM,
        FREE_MEM,
        EXIT,
        PRINT_CHAR,
        READ_CHAR,
        OPEN_FILE,
        READ_FILE,
        WRITE_FILE,
        CLOSE_FILE,
    }
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
