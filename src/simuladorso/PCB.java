package simuladorso;

public final class PCB {
	private int pid;
	private int numberOfInstructions;
	private int priority;
	private int arrivalTime;
	public int programCounter;
	private ProcessState state;

	public PCB(int pid, int priority, int arrivalTime, ProcessState state, int numberOfInstructions) {
		this.pid = pid;
		this.state = state;
		this.priority = priority;
		this.arrivalTime = arrivalTime;
		this.programCounter = 0;
		// this.programCounter = programCounter;
		this.numberOfInstructions = numberOfInstructions;
	}

	public int getPid() {
		return this.pid;
	}

	public ProcessState getState() {
		return this.state;
	}

	public void setState(ProcessState state) {
		this.state = state;
	}
	public int getNumberOfInstructions(){
		return this.numberOfInstructions;
	}
}