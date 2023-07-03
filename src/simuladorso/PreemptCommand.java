package simuladorso;

public class PreemptCommand extends Comando{
    
    public PreemptCommand(Scheduler receiver){
        super(receiver);
    }

    @Override
    public void execute() {
        Process process = receiver.getNextProcess(receiver.runningQueue);
        if(process == null){
            throw new IllegalStateException("No process to preempt in RunningQueue's");
        }
        
        if(process.isfinished()){
            process.setState(ProcessState.TERMINATED);
            receiver.finishedQueue.add(process);
        }
        else{
            process.setState(ProcessState.READY);
            receiver.readyQueue.add(process);
        }
    }

    @Override
    public Object getResponse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResponse'");
    }
}