package org.pampasim.gui.model;

public class CoreInfo {
    int coreId;
    boolean running;
    int runningProcessPid;

    public CoreInfo(int coreId, boolean running, int runningProcessPid) {
        this.coreId = coreId;
        this.running = running;
        this.runningProcessPid = runningProcessPid;
    }

    public int getCoreId() {
        return coreId;
    }

    public boolean isRunning() {
        return running;
    }

    public int getRunningProcessPid() {
        return runningProcessPid;
    }
}
