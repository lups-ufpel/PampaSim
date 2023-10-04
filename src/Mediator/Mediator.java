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

public class Mediator implements Runnable {
    private final HashMap<MediatorAction, Command> handlers = new HashMap<>();
    private final HashMap<MediatorComponent, Object> components = new HashMap<>();

    public Mediator() {
        handlers.put(MediatorAction.GET_PROCESS_BY_PID, new GetProcessByPid());
        handlers.put(MediatorAction.LIST_PROCESSES_PIDS, new ListProcessesPids());
        handlers.put(MediatorAction.LIST_NEW_PROCESSES, new ListNewProcesses());
        handlers.put(MediatorAction.LIST_READY_PROCESSES, new ListReadyProcesses());
        handlers.put(MediatorAction.LIST_RUNNING_PROCESSES, new ListRunningProcesses());
        handlers.put(MediatorAction.LIST_WAITING_PROCESSES, new ListWaitingProcesses());
        handlers.put(MediatorAction.LIST_TERMINATED_PROCESSES, new ListTerminatedProcesses());
        handlers.put(MediatorAction.START_VM, new StartVM());
        handlers.put(MediatorAction.STOP_VM, new StopVM());
        handlers.put(MediatorAction.SCHEDULER_SCHEDULE, new Schedule());
        handlers.put(MediatorAction.KERNEL_NEW_PROCESS, new NewProcess());
        handlers.put(MediatorAction.CORE_EXECUTE, new Execute());
        handlers.put(MediatorAction.LIST_CORES, new ListCores());
        handlers.put(MediatorAction.GET_NUM_CORES, new GetNumCores());
        handlers.put(MediatorAction.GET_AVAILABLE_PID, new GetAvailablePid());
        //handlers.put(MediatorAction.UPDATE_CORES_INFO, new UpdateCoresInfo());
    }

    @Override
    public void run() {

    }

    public void registerComponent(MediatorComponent componentType, Object component) {
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

    public Object invoke(MediatorAction action) {
        return this.invoke(new Message(action, null));
    }

    public Object invoke(MediatorAction action, Object[] parameters) {
        return this.invoke(new Message(action, parameters));
    }
}
