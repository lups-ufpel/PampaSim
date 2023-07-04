package simuladorso;

import java.util.ArrayList;

import simuladorso.Message.Message;
import simuladorso.Message.MessageHandler;

public class RoundRobinScheduler extends Scheduler implements MessageHandler {
    private int timeSlice;

	public RoundRobinScheduler(int timeSlice){
		super();
		this.timeSlice = timeSlice;
	}
    public int getTimeSlice() {
		return timeSlice;
	}

	public void setTimeSlice(int timeSlice) {
		this.timeSlice = timeSlice;
	}

	@Override
	public void addProcess(Process processPcb){
		ArrayList<Process> processQueue;
		switch (processPcb.getState()) {
			case READY:
				processQueue = getReadyQueue();
				break;
			case RUNNING:
				processQueue = getRunningQueue();
				break;
			case TERMINATED:
				processQueue = getFinishedQueue();
				break;
			default:
				throw new IllegalStateException("Process Illegal State");
		}
		processQueue.add(processPcb);
	}

	@Override
	public Process getNextProcess(ArrayList<Process> queue) {
		return queue.remove(0);
	}

	@Override
	public void execute(Message msg) {
		msg.execute();
	}

}
