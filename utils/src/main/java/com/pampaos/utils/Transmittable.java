package com.pampaos.utils;

public interface Transmittable {
    public String toJson();
    public static Transmittable fromJson(String json) {
        return null;
    };
}
