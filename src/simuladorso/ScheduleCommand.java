package simuladorso;

public class ScheduleCommand extends Comando{

    private ProcessResponse response;
    public ScheduleCommand(Scheduler receiver){
        super(receiver);
    } 
    @Override
    public void execute() {
        Process process = receiver.getNextProcess(receiver.getReadyQueue());
        process.setState(ProcessState.RUNNING);
        receiver.addProcess(process);
        this.response = new ProcessResponse(process);
    }
    public Object getResponse(){
        return response;
    }
}
