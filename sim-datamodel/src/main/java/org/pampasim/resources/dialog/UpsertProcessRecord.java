package org.pampasim.resources.dialog;

import org.pampasim.resources.Process;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;

public record UpsertProcessRecord(
        int start,
        int duration,
        int priority,
        String color,
        Map<Class<?>, Object> moduleInfo,
        boolean delete
) {
    public static UpsertProcessRecord fromProcCreationData(Process.CreationData creationData, String color) {
        return new UpsertProcessRecord(
                creationData.arrivalTick(),
                creationData.durationTicks(),
                creationData.startPriority(),
                color,
                (Map<Class<?>, Object>) creationData.moduleCreationData(),
                false
        );
    }
}
