public final class Kernel {
	//public Scheduler scheduler;
	public Scheduler scheduler;
	public static final int timeSlice = 2;
	private static int pid = 0;
	public Kernel(){
		this.scheduler = new Scheduler(/*timeSlice*/);
	}
	void fork(){
		Process pcb = new Process(requestPid(), 1,ProcessState.READY, 10);
		scheduler.addNewProcess(pcb);
	}
	public int requestPid(){
		return Kernel.pid++;
	}
}