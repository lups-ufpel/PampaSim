package org.pampasim.SimCore;

import org.pampasim.SimResources.Process;

public interface ProcessEventInfo extends EventInfo {
    Process getProcess();

    static ProcessEventInfo of(final EventListener<? extends EventInfo> listener, final Process process) {
        return new ProcessEventInfo() {
            @Override public Process getProcess() {
                return process;
            }
            @Override public EventListener<? extends EventInfo> getListener() {
                return listener;
            }
        };
    }
}
