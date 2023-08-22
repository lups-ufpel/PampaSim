package simuladorso.Os;

public class Os {
    private Kernel kernel;
    private Scheduler scheduler;

    public Os(int numCores) {
        this.kernel = new Kernel();
        this.scheduler = new Scheduler(this.kernel, numCores);
    }

    public Kernel getKernel() {
        return kernel;
    }

    public void setKernel(Kernel kernel) {
        this.kernel = kernel;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
