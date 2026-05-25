package org.pampasim.resources.dialog;

import java.util.Map;

public record CreateProcessRecord(
        int start,
        int duration,
        int priority,
        String color,
        Map<Class<?>, Object> moduleInfo
) { }
