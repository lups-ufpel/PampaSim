package simuladorso;

import java.util.UUID;

import simuladorso.Commands.ForkCommand;
import simuladorso.Commands.PreemptCommand;
import simuladorso.Commands.ScheduleCommand;
import simuladorso.Message.Message;

public final class Kernel {
	private static Kernel instance;
	
	private Kernel() {
	}

	public static Kernel getInstance() {
		if (instance == null) 
			instance = new Kernel();
		return instance;
	}
	//Quando um processo é criado, o sistema o coloca em uma lista de processos no estado  de pronto
	//onde aguarda uma oportunidade para ser executado
	public void fork(Scheduler scheduler) {
		Process process = new Process(requestPid(), 1, ProcessState.READY, 9);
		Message novMensagem = new ForkCommand(scheduler, process);
		sendMessage(novMensagem);
	}
	
	//Requisita o próximo processo a ser executado de acordo com o algoritmo de escalonamento
	public Process scheduleProcess(Scheduler scheduler){
		Process process;
		Message mensagem = new ScheduleCommand(scheduler);
		sendMessage(mensagem);
		Object response = mensagem.getResponse();
		if(response instanceof ProcessResponse){
			process = ((ProcessResponse) response).getProcess();
			if(process == null){
				throw new IllegalStateException("no process in readyQueue's");
			}
			return process;
		}
		throw new IllegalStateException("Schedule command invalid response, expected: ProcessResponse received:"+response.toString());
	}

	//Remove o processo da lista executando e adiciona a lista de prontos ou finalizados
	public void preemptProcess(Scheduler receiver){
		Message novMensagem = new PreemptCommand(receiver);
		sendMessage(novMensagem);
	}

	public void sendMessage(Message msg){
		msg.execute();
	}

	// public boolean finishExecution(){
	// 	return scheduler.verifyAllQueues();
	// }
	//Cada  processo  criado  pelo  simulador  recebe  uma  identificação  única  (PID  —  process  identification),
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