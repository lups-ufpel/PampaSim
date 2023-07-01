import java.util.ArrayList;

public class Scheduler {
	private int quantum;
	private SchedulerQueue readyQueue;
	private SchedulerQueue runningQueue;
	private SchedulerQueue finishedQueue;
	public Scheduler(int quantum){
		this.readyQueue = new SchedulerQueue();
		this.runningQueue = new SchedulerQueue();
		this.finishedQueue = new SchedulerQueue();
		this.quantum = quantum;
	}
	public void addNewProcess(PCB processPcb){
		this.readyQueue.addProcess(processPcb);
	}
	public void runProcess(){
		if(this.readyQueue.isEmpty()){
			throw new IllegalStateException("no process in readyQueue's");
		}
		PCB proc = selectNextProcess(readyQueue);
		this.runningQueue.addProcess(proc);
	}
	public PCB executeProcess(){
		if(this.readyQueue.isEmpty()){
			throw new IllegalStateException("no process in readyQueue's");
		}
		PCB proc = selectNextProcess(readyQueue);
		this.runningQueue.addProcess(proc);
		return proc;
	}
	public void preemptProcess() {
		PCB proc = selectNextProcess(runningQueue);
		if (proc == null) {
			throw new IllegalStateException("no process executing in RunningQueue's");
		}
		if(proc.getNumberOfInstructions() <= proc.programCounter){
			proc.setState(ProcessState.TERMINATED);
			this.finishedQueue.addProcess(proc);
		}
		else{
			proc.setState(ProcessState.READY);
			this.readyQueue.addProcess(proc);
		}
		
	}
	public boolean verifyAllQueues(){
		if(readyQueue.isEmpty()){
			if(runningQueue.isEmpty()){
				if(!finishedQueue.isEmpty()){
					while(!finishedQueue.isEmpty()){
						PCB pcb = selectNextProcess(this.finishedQueue);
						System.out.println("Processo de PID: "+pcb.getPid()+" executou com sucesso!");
					}
					return true;
				}
				else{
					throw new IllegalStateException("System is idle but no process has executed");
				}
			}
			return false;
		}
		return false;
	}
	public PCB selectNextProcess(SchedulerQueue queue){
		return queue.getProcess();
	}
}