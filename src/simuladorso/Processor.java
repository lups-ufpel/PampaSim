package simuladorso;

public class Processor {
    private static Processor instance;
    private Cpu[] cores;

    private Processor() {
        cores = new Cpu[1];
    }

    public static Processor getInstance() {
        if (instance == null)
            instance = new Processor();
        return instance;
    }


}
