package org.simuladorso.Mediator;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
public interface Mediator {

    static final Logger LOGGER = LoggerFactory.getLogger(Mediator.class.getSimpleName());
    public void registerComponent(Component componentType, Object component);

    public Object invoke(Message message);

    public Object invoke(Action action);

    public Object invoke(Action action, Object[] parameters);

    //Mediator interface is just used as a namespace for the enum.
    public enum Action {
        KERNEL_GET_LIST,
        KERNEL_GET_PROCESS,
        KERNEL_NEW_PROCESS,
        CORE_EXECUTE,
        PROCESS_GET_PID,
        SCHEDULER_SCHEDULE,
        SCHEDULER_ADD_TO_QUEUE,
        SBYTE_GET_VALUE,
        SBYTE_SET_VALUE,
        GET_PROCESS_BY_PID,
        LIST_PROCESSES_PIDS,
        LIST_NEW_PROCESSES,
        LIST_READY_PROCESSES,
        LIST_RUNNING_PROCESSES,
        LIST_WAITING_PROCESSES,
        LIST_TERMINATED_PROCESSES,
        LIST_CORES,
        GET_NUM_CORES,
        START_VM,
        STOP_VM,
        GET_AVAILABLE_PID,
        GET_TIME,
        UPDATE_CORES_INFO,
        MEM_ALLOC,
        MEM_FREE,
        
    }
    public enum Component {
        VM,
        OS,
        GUI,
        CLI,
        SCHEDULER,
        MEM_MANAGER
    }
}
