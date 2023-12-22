package com.pampaos.utils;

public class Process implements Transmittable {
    public String toJson() {
        return "";
    }

    public static Process fromJson(String json) {
        return new Process();
    }
}
