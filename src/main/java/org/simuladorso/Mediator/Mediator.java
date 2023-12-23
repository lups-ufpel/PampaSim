package org.simuladorso.Mediator;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.function.Consumer;

public interface Mediator {

    Logger LOGGER = LoggerFactory.getLogger(Mediator.class.getSimpleName());

    static final Mediator instance = new MediatorDefault();

    public static Mediator getInstance() {return instance; }
    public void registerComponent(Object component, Component componentType);

    public Object invoke(Message message);

    public Object invoke(Action action);

    public Object invoke(Action action, Object[] parameters);

    public void send(Object sender, Action action);

    //Mediator interface is just used as a namespace for the enum.
    public enum Action {

        CREATE,
        VISUALIZE,
        EXECUTE,
        RUN,
        ON_THIS_PROCESS_DISPATCHED,
        ON_THIS_PROCESS_INTERRUPTED,
        ON_THIS_PROCESS_SUBMITTED,
        ON_THIS_PROCESS_FINISHED,
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
        GET_SIM_STATUS,
        GET_AVAILABLE_PID,
        GET_TIME,
        UPDATE_CORES_INFO,
        MEM_ALLOC,
        MEM_FREE,
        
    }
    public enum Component {
        VM,
        KERNEL,
        GUI,
        CLI,
        SCHEDULER,
        MEM_MANAGER
    }
}
