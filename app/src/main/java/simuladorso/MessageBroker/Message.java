package simuladorso.MessageBroker;

import java.util.HashMap;

public class Message {
    private MessageType action;
    private HashMap<MessageParam, Object> parameters;

    /**
     * Method call with parameters and with no receiver to modify
     * 
     * @param action
     * @param parameters
     */
    public Message(MessageType action, HashMap<MessageParam, Object> parameters) {
        this.action = action;
        this.parameters = parameters;
    }

    public MessageType getAction() {
        return action;
    }

    public HashMap<MessageParam, Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("Action %s sended", this.getAction());
    }
}
