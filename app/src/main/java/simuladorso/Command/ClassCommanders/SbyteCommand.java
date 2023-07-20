package simuladorso.Command.ClassCommanders;

import simuladorso.Command.Errors.IllegalMethodCall;
import simuladorso.Command.MainCommand.Command;
import simuladorso.Command.MainCommand.Message;
import simuladorso.VirtualMachine.Sbyte;

public class SbyteCommand implements Command {

    private Sbyte sbyte;

    public SbyteCommand(Sbyte sbyte) {
        this.sbyte = sbyte;
    }

    public Object execute(Message msg) {

        switch (msg.getMethodName()) {
            case "getValue":
                return sbyte.getValue();
            case "setValue":
                if (msg.getParam() instanceof String || msg.getParam() instanceof Integer) {
                    sbyte.setValue((String) msg.getParam());
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
