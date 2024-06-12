package org.pampasim.Os;

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
    private InterruptionTable interruption_state;

    public Interruption(){
        this.interruption_state = InterruptionTable.NONE;
    }
    
    public InterruptionTable get() {
        return interruption_state;
    }
    public void set(InterruptionTable interruption_state) {
        this.interruption_state = interruption_state;
    }
}
