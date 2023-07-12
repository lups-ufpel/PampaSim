package Command;

import Command.MainCommand.Message;

public class IllegalMethodCall extends RuntimeException {

    public IllegalMethodCall(String className, String methodName, Message msg) {
        System.out.println("\n===================================== FATAL ERROR =====================================");
        System.out.println("*\tIllegal method call on Command/MainCommand/Message.java");
        System.out.println("*\tNo method \"" + methodName + "(" + msg.getParam() + ")\" found");
        System.out.println("*\tVerify the method name and parameters given.");
        System.out.println("*");
        System.out.println("*\tMessage content:");
        System.out.println("*\t\tSender: " + msg.getSender());
        System.out.println("*\t\tCalled method: " + msg.getMethodName());
        System.out.println("*\t\tParams: " + msg.getParam());
        System.out.println("*\t\tReceiver: " + msg.getReceiver());
        System.out.println("\n=======================================================================================");

    }
}
