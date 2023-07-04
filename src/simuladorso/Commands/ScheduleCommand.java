package simuladorso.Commands;

import simuladorso.Process;
import simuladorso.ProcessResponse;
import simuladorso.ProcessState;
import simuladorso.Scheduler;

public class ScheduleCommand extends Command {

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
