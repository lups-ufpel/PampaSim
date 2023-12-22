package com.pampaos.utils;

public enum Component implements Transmittable {
    VM,
    GUI,
    Mediator,
    OS,
    Scheduler;

    @Override
    public String toJson() {
        return null;
    }

    public static Component fromJson(String json) {
        return
    }
}
