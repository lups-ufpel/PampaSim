package simuladorso.Command.Errors;

import simuladorso.Command.MainCommand.Message;

public class IllegalMethodCall extends RuntimeException {
    /**
     * No method found with the right name and parameters
     * 
     * @param className
     * @param methodName
     * @param msg
     */
    public IllegalMethodCall(String className, Message msg) {
        Object receiver = msg.getReceiver() == null ? "void" : msg.getReceiver();

        System.out.println("\n===================================== FATAL ERROR =====================================");
        System.out.println("*\tIllegal method call on Command/ClassCommanders/" + className + "Command.java");
        System.out.println("*");
        System.out.println(
                "*\tNo method \"" + msg.getMethodName() + "(" + msg.getParam() + ") -> Receiver(" + receiver
                        + ")\" found");
        System.out.println("*");
        System.out.println("*\tVerify the method name, receiver and parameters given.");
        System.out.println("*");
        System.out.println("*\tMessage content:");
        System.out.println("*\t\tSender: " + msg.getSender());
        System.out.println("*\t\tCalled method: " + msg.getMethodName());
        System.out.println("*\t\tParams: " + msg.getParam());
        System.out.println("*\t\tReceiver: " + msg.getReceiver());
        System.out.println("\n=======================================================================================");

    }
}
