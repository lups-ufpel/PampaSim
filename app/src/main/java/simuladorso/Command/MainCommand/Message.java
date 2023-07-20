package simuladorso.Command.MainCommand;

public class Message {
    private Object sender;
    private String methodName;
    private Object param;
    private Object receiver;

    /**
     * Method call without parameters and with no receiver to modify
     * 
     * @param String methodName
     */
    public Message(String methodName) {
        this.methodName = methodName;
        this.sender = null;
        this.param = null;
        this.receiver = null;
    }

    /**
     * Method call with parameters and with no receiver to modify
     * 
     * @param String methodName
     * @param Object param
     */
    public Message(String methodName, Object param) {
        this.methodName = methodName;
        this.param = param;
        this.sender = null;
        this.receiver = null;
    }

    /**
     * Method call with parameters and with a receiver to be modified
     * 
     * @param String   methodName
     * @param param
     * @param receiver
     */
    public Message(String methodName, Object param, Object receiver) {
        this.methodName = methodName;
        this.param = param;
        this.receiver = receiver;
        this.sender = null;
    }

    /**
     * Method call with parameters, with a receiver to be modified and a sender
     * 
     * @param sender
     * @param methodName
     * @param param
     * @param receiver
     */
    public Message(Object sender, String methodName, Object param, Object receiver) {
        this.sender = sender;
        this.methodName = methodName;
        this.param = param;
        this.receiver = receiver;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object getSender() {
        return sender;
    }

    public Object getParam() {
        return param;
    }

    public Object getReceiver() {
        return receiver;
    }
}
