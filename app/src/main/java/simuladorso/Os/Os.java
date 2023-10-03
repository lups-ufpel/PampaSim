package simuladorso.Os;

import simuladorso.Mediator.Mediator;
import simuladorso.Mediator.MediatorAction;

public class Os {
    private Kernel kernel;
    private Scheduler scheduler;

    public Os(Mediator mediator) {
        this.kernel = new Kernel(mediator);
        this.scheduler = new Scheduler(this.kernel, (Integer) mediator.invoke(MediatorAction.GET_NUM_CORES), mediator);
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
