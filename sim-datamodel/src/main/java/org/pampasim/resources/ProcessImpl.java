package org.pampasim.resources;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.utils.PidAllocator.Pid;

import java.util.ArrayList;
import java.util.Map;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProcessImpl implements Process {
    @Getter
    @EqualsAndHashCode.Include
    private final CreationData creationData;

    private final Logger LOGGER = LogManager.getLogger(ProcessImpl.class);
    @EqualsAndHashCode.Include
    private final Pid pid;
    State state;
    private int currExecTime; // elapsed execution time
    @Setter
    int burstTime; // length of the next "turn" on the processor -- incorrect, but true for now
    @Setter
    private int priority;
    private int waitTime;
    @Setter
    private int endTime;

    private final ArrayList<ProcessModuleInfo> moduleInfo;

    public ProcessImpl(Pid pid, CreationData creationData) {
        this.pid = pid;
        this.state = State.NEW;
        this.creationData = creationData;
        this.burstTime = creationData.durationTicks();
        this.currExecTime = 0;
        this.moduleInfo = new ArrayList<>();
    }

    public int getRemainingExecutionTime() {
        return creationData.durationTicks() - currExecTime;
    }

    public void forwardProcessExecution() {
        this.currExecTime +=1;
        this.burstTime -= 1;
    }

    public void forwardWaitingTime() {
        this.waitTime += 1;
    }

    public boolean isFinished() {
        return getRemainingExecutionTime() <= 0;
    }

    public void setState(ProcessImpl.State state) {
        this.state = state;
        LOGGER.debug("Process of PID {} transitioned to state {}", pid, state);
    }

    public void addModuleInfo(ProcessModuleInfo moduleInfo) {
        this.moduleInfo.add(moduleInfo);
    }

    public <T extends ProcessModuleInfo> T getModuleInfo(Class<T> moduleClass) {
        return moduleInfo.stream()
                .filter(moduleClass::isInstance)
                .map(moduleClass::cast)
                .findFirst()
                .orElse(null);
    }

}
