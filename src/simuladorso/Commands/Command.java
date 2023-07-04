package simuladorso.Commands;

import simuladorso.Scheduler;
import simuladorso.Message.Message;

public abstract class Command implements Message {
    protected Scheduler receiver;
    public Command(Scheduler receiver){
        this.receiver = receiver;
    }
}
