package Kernel;

public interface Process extends Comparable<Process> {

    // A enum State dentro da classe Process é uma prática interessante pois facilita a compreensao
    // de que o estado é algo único do processo e não de outras classes.
    // Para definir um estado, basta usar Process.State.<ESTADO> (ex: Process.State.NEW)
    enum State {
        /**
         * The Process has been just instantiated but not assigned to CPU Core.
         */
        NEW,

        /**
         * The Process has been assigned to a Scheduler Queue to be later executed.
         */
        READY,
        /**
         * The Process is currently being executed by a CPU Core.
         */
        RUNNING,

        /**
         * The Process is currently waiting for an I/O operation to be completed.
         */
        WAITING,
        
        /**
         * The Process has been terminated.
         */
        TERMINATED
    }


    int getPid();

    int getPriority();

    Process setPriority(int priority);

    int getCpuPercentage();

    int getArrivalTime();

    State getState();

    int compareTo(Process process);


    public class Interruption {
        private int type;
        private int value;

        public Interruption() {
            this.type = 0;
            this.value = 0;
        }

        public Interruption(int type, int value) {
            this.type = type;
            this.value = value;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            if (type >= 0 && type <= 3) {
                this.type = type;
            }
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            if (value >= 0 && value <= 255) {
                this.value = value;
            }
        }
    }
}