package simuladorso;

import java.util.UUID;
import java.math.BigInteger;

public final class Kernel {
	private static Kernel instance;
	public Scheduler scheduler;
	private int timeSlice;
	//private static int pid = 1;
	
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
		//return Kernel.pid++;

		UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        // Generate a fixed 6-digit number from the UUID
        long fixedNumber = Math.abs(mostSignificantBits + leastSignificantBits) % 1000000;
        // Format the fixed number as a 6-digit string
        String fixedUUID = String.format("%06d", fixedNumber);
		return fixedUUID;
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