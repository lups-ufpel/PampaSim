public class Scheduler {
	// private int quantum;
	private FIFOQueue readyQueue;
	private FIFOQueue runningQueue;
	private FIFOQueue finishedQueue;

	public Scheduler(/*int quantum*/){
		this.readyQueue = new FIFOQueue();
		this.runningQueue = new FIFOQueue();
		this.finishedQueue = new FIFOQueue();
		// this.quantum = quantum;
	}

	public void addNewProcess(Process processPcb) {
		this.readyQueue.addProcess(processPcb);
	}

	public void runProcess() {
		if (this.readyQueue.isEmpty()) {
			throw new IllegalStateException("no process in readyQueue's");
		}
		Process proc = selectNextProcess(readyQueue);
		this.runningQueue.addProcess(proc);
	}

	public Process executeProcess() {
		if (this.readyQueue.isEmpty()) {
			throw new IllegalStateException("no process in readyQueue's");
		}
		Process proc = selectNextProcess(readyQueue);
		this.runningQueue.addProcess(proc);
		return proc;
	}

	public void preemptProcess() {
		Process proc = selectNextProcess(runningQueue);
		if (proc == null) {
			throw new IllegalStateException("no process executing in RunningQueue's");
		}
		if (proc.getNumberOfInstructions() <= proc.programCounter) {
			proc.setState(ProcessState.TERMINATED);
			this.finishedQueue.addProcess(proc);
		} else {
			proc.setState(ProcessState.READY);
			this.readyQueue.addProcess(proc);
		}

	}

	public boolean verifyAllQueues() {
		if (readyQueue.isEmpty()) {
			if (runningQueue.isEmpty()) {
				if (!finishedQueue.isEmpty()) {
					while (!finishedQueue.isEmpty()) {
						Process pcb = selectNextProcess(this.finishedQueue);
						System.out.println("Processo de PID: " + pcb.getPid() + " executou com sucesso!");
					}
					return true;
				} else {
					throw new IllegalStateException("System is idle but no process has executed");
				}
			}
			return false;
		}
		return false;
	}

	public Process selectNextProcess(FIFOQueue queue) {
		return queue.getProcess();
	}
}