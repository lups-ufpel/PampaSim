package simuladorso;

import java.util.ArrayList;

public class RoundRobinScheduler extends Scheduler implements MessageHandler{
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
	public void addNewProcess(Process processPcb) {
		this.readyQueue.add(processPcb);
	}

	@Override
	public Process getNextProcess(ArrayList<Process> queue) {
		return queue.remove(0);
	}
	@Override
	public void execute(Mensagem msg) {
		msg.execute();
	}

}
