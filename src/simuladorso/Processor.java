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

    public void cycle(Process process) {
        cores.get(0).cycle(process);
    }
}
