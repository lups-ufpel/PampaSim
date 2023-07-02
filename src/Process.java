
public final class Process {
	private String pid;
	private int numberOfInstructions;
	private int priority;
	//private int arrivalTime;
	public int programCounter;
	private ProcessState state;

	public Process(String pid, int priority, ProcessState state, int numberOfInstructions) {
		this.pid = pid;
		this.state = state;
		this.priority = priority;
		this.programCounter = 0;
		this.numberOfInstructions = numberOfInstructions;
	}

	public String getPid() {
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