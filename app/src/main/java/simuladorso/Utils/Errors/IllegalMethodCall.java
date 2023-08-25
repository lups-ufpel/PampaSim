package simuladorso.Utils.Errors;

import simuladorso.Mediator.Message;

public class IllegalMethodCall extends Exception {
    public IllegalMethodCall(String message) {
        super(message);
    }
    public IllegalMethodCall(String className, Message message) {
        super(String.format("%s : %s", className, message.toString()));
    }
}
