package simuladorso.Commands;

import simuladorso.Process;
import simuladorso.Scheduler;

public class ForkCommand extends Command{

    private Process process;

    public ForkCommand(Scheduler receiver, Process process){
        super(receiver);
        this.process = process;
    }
    @Override
    public void execute() {
        receiver.addProcess(process);
    }
    @Override
    public Object getResponse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResponse'");
    }

}
