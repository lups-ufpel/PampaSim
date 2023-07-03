package simuladorso;

public class ScheduleCommand extends Comando{

    private ProcessResponse response;
    public ScheduleCommand(Scheduler receiver){
        super(receiver);
    } 
    @Override
    public void execute() {
        Process process = receiver.readyQueue.remove(0);
        process.setState(ProcessState.RUNNING);
        this.receiver.runningQueue.add(process);
        this.response = new ProcessResponse(process);
    }
    public Object getResponse(){
        return response;
    }
}
