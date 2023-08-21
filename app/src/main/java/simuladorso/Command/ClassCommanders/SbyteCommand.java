package simuladorso.Command.ClassCommanders;

import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Command;
import simuladorso.MessageBroker.Message;
import simuladorso.VirtualMachine.Sbyte;

public class SbyteCommand implements Command {

    private Sbyte sbyte;

    public SbyteCommand(Sbyte sbyte) {
        this.sbyte = sbyte;
    }

    public Object execute(Message msg) throws IllegalMethodCall, IllegalClassCall {

        switch (msg.getAction()) {
            case "getValue":
                return sbyte.getValue();
            case "setValue":
                if (msg.getParameters() instanceof String || msg.getParameters() instanceof Integer) {
                    sbyte.setValue((String) msg.getParameters());
                } else {
                    throw new IllegalMethodCall("Sbyte", msg);
                }
                break;
            default:
                throw new IllegalMethodCall("Sbyte", msg);
        }

        return null;
    }

}
