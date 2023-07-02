package simuladorso;

public final class Kernel {
	private static Kernel instance;
	public Scheduler scheduler;
	private int timeSlice;
	private static int pid = 1;
	
	private Kernel() {
		timeSlice = 2;
		scheduler = new Scheduler(timeSlice);
	}

	public static Kernel getInstance() {
		if (instance == null) 
			instance = new Kernel();
		return instance;
	}

	public void fork() {
		PCB pcb = new PCB(requestPid(), 1, 0,ProcessState.READY, 10);
		scheduler.addNewProcess(pcb);
	}

	public int requestPid(){
		return Kernel.pid++;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public int getTimeSlice() {
		return timeSlice;
	}

	public void setTimeSlice(int timeSlice) {
		this.timeSlice = timeSlice;
	}
}