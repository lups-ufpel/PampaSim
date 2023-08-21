package simuladorso.MessageBroker;

public class Message {
    private String action;
    private Object parameters;
    private String receiver;
    private String sender;

    /**
     * Method call without parameters and with no receiver to modify
     * 
     * @param action
     */
    public Message(String action) {
        this.action = action;
        this.sender = null;
        this.parameters = null;
        this.receiver = null;
    }

    /**
     * Method call with parameters and with no receiver to modify
     * 
     * @param action
     * @param parameters
     */
    public Message(String action, Object parameters) {
        this.action = action;
        this.parameters = parameters;
        this.sender = null;
        this.receiver = null;
    }

    /**
     * Method call with parameters and with a receiver to be modified
     * 
     * @param action
     * @param parameters
     * @param receiver
     */
    public Message(String action, Object parameters, String receiver) {
        this.action = action;
        this.parameters = parameters;
        this.receiver = receiver;
        this.sender = null;
    }

    /**
     * Method call with parameters, with a receiver to be modified and a sender
     *
     * @param action
     * @param parameters
     * @param receiver
     * @param sender
     */
    public Message(String action, Object parameters, String receiver, String sender) {
        this.action = action;
        this.parameters = parameters;
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getAction() {
        return action;
    }

    public Object getParameters() {
        return parameters;
    }

    public String getReceiverId() {
        return receiver;
    }
    public String getSenderId() {
        return sender;
    }

    @Override
    public String toString() {
        return String.format("Action %s sended by %s to %s", getAction(), getSenderId(), getReceiverId());
    }
}
