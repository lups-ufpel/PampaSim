package simuladorso;

import java.util.ArrayList;

public class Processor {
    private static Processor instance;
    private ArrayList<Cpu> cores;

    private Processor() {
        cores = new ArrayList<Cpu>();
        cores.add(0, new Cpu());
    }

    public static Processor getInstance() {
        if (instance == null)
            instance = new Processor();
        return instance;
    }

    public boolean cycle(Process process) {
        if(process.isfinished()){
            return false;
        }
        cores.get(0).cycleWithThreadSleep(process);
        return true;
    }
}
