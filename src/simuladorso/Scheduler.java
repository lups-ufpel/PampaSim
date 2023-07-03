package simuladorso;

import java.util.ArrayList;

public abstract class Scheduler {
	private static final int queueSize = 15;
	protected ArrayList<Process> readyQueue;
	protected ArrayList<Process> runningQueue;
	protected ArrayList<Process> finishedQueue;

	public Scheduler() {
		this.readyQueue = new ArrayList<Process>(queueSize);
		this.runningQueue = new ArrayList<Process>(queueSize);
		this.finishedQueue = new ArrayList<Process>(queueSize);
	}
	public abstract void addNewProcess(Process processPcb);
	public abstract Process getNextProcess(ArrayList<Process> queue);
	public boolean verifyAllQueues() {
		if (readyQueue.isEmpty()) {
			if (runningQueue.isEmpty()) {
				if (!finishedQueue.isEmpty()) {
					while (!finishedQueue.isEmpty()) {
						Process process = getNextProcess(this.finishedQueue);
						System.out.println("Processo de PID: " + process.getPid() + " executou com sucesso!");
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
	
}