package simuladorso;

import java.util.UUID;

public final class Kernel {
	private static Kernel instance;
	public Scheduler scheduler;
	//private static int pid = 1;
	
	private Kernel() {
		scheduler = new Scheduler(2);
	}

	public static Kernel getInstance() {
		if (instance == null) 
			instance = new Kernel();
		return instance;
	}

	public void fork() {
		Process process = new Process(requestPid(), 1, ProcessState.READY, 10);
		scheduler.addNewProcess(process);
	}

	public String requestPid(){
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
}