package simuladorso.Mediator;

import java.util.HashMap;

public class Message {
    private MediatorAction action;
    private Object[] parameters;
    private HashMap<MediatorComponent, Object> components;

    public Message(MediatorAction action, Object[] parameters, HashMap<MediatorComponent, Object> components) {
        this.action = action;
        this.parameters = parameters;
        this.components = components;
    }

    public Message(MediatorAction action, Object[] parameters) {
        this.action = action;
        this.parameters = parameters;
        this.components = null;
    }

    public MediatorAction getAction() {
        return action;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public HashMap<MediatorComponent, Object> getComponents() {
        return components;
    }

    public void setComponents(HashMap<MediatorComponent, Object> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return String.format("Action %s sended", this.getAction());
    }
}
