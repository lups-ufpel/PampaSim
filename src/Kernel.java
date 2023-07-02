import java.math.BigInteger;
import java.util.UUID;

public final class Kernel {
	//public Scheduler scheduler;
	public Scheduler scheduler;
	public static final int timeSlice = 2;
	public Kernel(){
		this.scheduler = new Scheduler(/*timeSlice*/);
	}
	//Quando um processo é criado, o sistema o coloca em uma lista de processos no estado de pronto, 
	// onde aguarda uma oportunidade para ser executado
	void fork(){
		Process pcb = new Process(requestPid(), 1,ProcessState.READY, 10);
		scheduler.addNewProcess(pcb);
	}
	//Cada processo criado pelo simulador recebe uma identificação única (PID)
	//O  número  é  criado  a  partir  da hora (hora, minutos e segundos) corrente.
	public String requestPid(){

		UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        // Generate a fixed 6-digit number from the UUID
        long fixedNumber = Math.abs(mostSignificantBits + leastSignificantBits) % 1000000;
        // Format the fixed number as a 6-digit string
        String fixedUUID = String.format("%06d", fixedNumber);
		return fixedUUID;
	}
}