package com.pampaos.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Message implements Transmittable {
    private Component origin;
    private Component destination;
    private Action action;
    private List<Transmittable> arguments;

    public Message(Component origin, Component destination, Action action, List<Transmittable> arguments) {
        this.origin = origin;
        this.destination = destination;
        this.action = action;
        this.arguments = arguments;
    }

    @Override
    public String toJson() {
        JSONObject message = new JSONObject();
        message.put("origin", this.origin.toString());
        message.put("destination", this.destination.toString());
        message.put("action", this.action.toString());
        message.put("arguments", new JSONArray(this.arguments));
        return message.toString();
    }

    public static Message fromJson(String json) {

        return new Message();
    }
}
