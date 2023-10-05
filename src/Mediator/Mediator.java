package Mediator;

import Mediator.Handlers.Core.Execute;
import Mediator.Handlers.Core.GetNumCores;
import Mediator.Handlers.Core.ListCores;
//import Mediator.Handlers.Gui.UpdateCoresInfo;
import Mediator.Handlers.Kernel.*;
import Mediator.Handlers.Process.GetAvailablePid;
import Mediator.Handlers.Scheduler.Schedule;
import Mediator.Handlers.VM.StartVM;
import Mediator.Handlers.VM.StopVM;
import Logger.Logger;
import Utils.Command;

import java.util.HashMap;

import javax.swing.Action;

public class Mediator implements Runnable {
    private final HashMap<Action, Command> handlers = new HashMap<>();
    private final HashMap<Component, Object> components = new HashMap<>();

    public enum Action {
        KERNEL_GET_LIST,
        KERNEL_GET_PROCESS,
        KERNEL_NEW_PROCESS,
        CORE_EXECUTE,
        PROCESS_GET_PID,
        SCHEDULER_SCHEDULE,
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
        UPDATE_CORES_INFO,
    }
    public enum Component {
        VM,
        OS,
        GUI,
        CLI,
        SCHEDULER
    }
    public Mediator() {
        handlers.put(Action.GET_PROCESS_BY_PID, new GetProcessByPid());
        handlers.put(Action.LIST_PROCESSES_PIDS, new ListProcessesPids());
        handlers.put(Action.LIST_NEW_PROCESSES, new ListNewProcesses());
        handlers.put(Action.LIST_READY_PROCESSES, new ListReadyProcesses());
        handlers.put(Action.LIST_RUNNING_PROCESSES, new ListRunningProcesses());
        handlers.put(Action.LIST_WAITING_PROCESSES, new ListWaitingProcesses());
        handlers.put(Action.LIST_TERMINATED_PROCESSES, new ListTerminatedProcesses());
        handlers.put(Action.START_VM, new StartVM());
        handlers.put(Action.STOP_VM, new StopVM());
        handlers.put(Action.SCHEDULER_SCHEDULE, new Schedule());
        handlers.put(Action.KERNEL_NEW_PROCESS, new NewProcess());
        handlers.put(Action.CORE_EXECUTE, new Execute());
        handlers.put(Action.LIST_CORES, new ListCores());
        handlers.put(Action.GET_NUM_CORES, new GetNumCores());
        handlers.put(Action.GET_AVAILABLE_PID, new GetAvailablePid());
        //handlers.put(Action.UPDATE_CORES_INFO, new UpdateCoresInfo());
    }

    @Override
    public void run() {

    }

    public void registerComponent(Component componentType, Object component) {
        if (componentType != null && component != null) {
            this.components.put(componentType, component);
        }
    }

    public Object invoke(Message message) {
        Logger.getInstance().debug(String.format("MessageBroker received: %s", message.toString()));

        Command executor = handlers.get(message.getAction());

        if (executor == null) {
            Logger.getInstance().error("Message action passed not implemented: " + message.getAction());
        }

        message.setComponents(this.components);

        return handlers.get(message.getAction()).execute(message);
    }

    public Object invoke(Action action) {
        return this.invoke(new Message(action, null));
    }

    public Object invoke(Action action, Object[] parameters) {
        return this.invoke(new Message(action, parameters));
    }
}
