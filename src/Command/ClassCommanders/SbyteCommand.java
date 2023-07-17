package Command.ClassCommanders;

import Command.Errors.IllegalMethodCall;
import Command.MainCommand.Command;
import Command.MainCommand.Message;
import VirtualMachine.Sbyte;

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
