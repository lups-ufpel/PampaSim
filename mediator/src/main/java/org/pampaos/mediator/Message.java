package org.simuladorso.mediator;

import java.util.HashMap;

public class Message {
    private Mediator.Action action;
    private Object[] parameters;
    private HashMap<Mediator.Component, Object> components;

    public Message(Mediator.Action action, Object[] parameters, HashMap<Mediator.Component, Object> components) {
        this.action = action;
        this.parameters = parameters;
        this.components = components;
    }

    public Message(Mediator.Action action, Object[] parameters) {
        this.action = action;
        this.parameters = parameters;
        this.components = null;
    }

    public Mediator.Action getAction() {
        return action;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public HashMap<Mediator.Component, Object> getComponents() {
        return components;
    }

    public void setComponents(HashMap<Mediator.Component, Object> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return String.format("Action %s sended", this.getAction());
    }
}
