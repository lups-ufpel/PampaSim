package simuladorso;

import java.util.ArrayList;

public abstract class Scheduler {
	private static final int MAX_QUEUE_SIZE = 15;
	private ArrayList<Process> readyQueue;
	private ArrayList<Process> runningQueue;
	private ArrayList<Process> finishedQueue;

	public Scheduler() {
		this.readyQueue = new ArrayList<Process>(MAX_QUEUE_SIZE);
		this.runningQueue = new ArrayList<Process>(MAX_QUEUE_SIZE);
		this.finishedQueue = new ArrayList<Process>(MAX_QUEUE_SIZE);
	}
	protected ArrayList<Process> getReadyQueue() {
        return readyQueue;
    }

    protected ArrayList<Process> getRunningQueue() {
        return runningQueue;
    }

    protected ArrayList<Process> getFinishedQueue() {
        return finishedQueue;
    }
	public abstract void addProcess(Process processPcb);
	public abstract Process getNextProcess(ArrayList<Process> queue);
	
	public boolean verifyAllQueues() {
		if (readyQueue.isEmpty()) {
			if (runningQueue.isEmpty()) {
				if (!finishedQueue.isEmpty()) {
					for (Process process : finishedQueue) {
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