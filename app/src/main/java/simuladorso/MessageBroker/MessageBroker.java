package simuladorso.MessageBroker;

import simuladorso.GUI.SimulatorGui;
import simuladorso.MessageBroker.Handlers.Core.Execute;
import simuladorso.MessageBroker.Handlers.Core.GetNumCores;
import simuladorso.MessageBroker.Handlers.Kernel.NewProcess;
import simuladorso.MessageBroker.Handlers.Scheduler.Schedule;
import simuladorso.Os.Kernel;
import simuladorso.Os.Scheduler;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Handlers.Gui.*;
import simuladorso.Utils.Command;
import simuladorso.VirtualMachine.VirtualMachine;

import java.util.HashMap;

public class MessageBroker implements Runnable {
    private final HashMap<MessageType, Command> handlers = new HashMap<>();
    private final HashMap<MessageParam, Object> params = new HashMap<>();
    private static MessageBroker instance;
    private VirtualMachine vm;
    private Kernel kernel;
    private Scheduler scheduler;
    private SimulatorGui gui;

    private MessageBroker() {
        handlers.put(MessageType.GET_PROCESS_BY_PID, new GetProcessByPid());
        handlers.put(MessageType.LIST_PROCESSES_PIDS, new ListProcessPids());
        handlers.put(MessageType.LIST_NEW_PROCESSES, new ListNewProcesses());
        handlers.put(MessageType.LIST_READY_PROCESSES, new ListReadyProcesses());
        handlers.put(MessageType.LIST_RUNNING_PROCESSES, new ListRunningProcesses());
        handlers.put(MessageType.LIST_WAITING_PROCESSES, new ListWaitingProcesses());
        handlers.put(MessageType.LIST_TERMINATED_PROCESSES, new ListTerminatedProcesses());
        handlers.put(MessageType.START_VM, new StartVM());
        handlers.put(MessageType.STOP_VM, new StopVM());
        handlers.put(MessageType.SCHEDULER_SCHEDULE, new Schedule());
        handlers.put(MessageType.KERNEL_NEW_PROCESS, new NewProcess());
        handlers.put(MessageType.CORE_EXECUTE, new Execute());
        handlers.put(MessageType.LIST_CORES, new ListCores());
        handlers.put(MessageType.GET_NUM_CORES, new GetNumCores());
    }

    public static MessageBroker getInstance() {
        if (instance == null)
            instance = new MessageBroker();
        return instance;
    }

    @Override
    public void run() {

    }

    public void setVm(VirtualMachine vm) {
        this.vm = vm;
        this.params.put(MessageParam.VM, this.vm);
    }

    public void setKernel(Kernel kernel) {
        this.kernel = kernel;
        this.params.put(MessageParam.KERNEL, this.kernel);
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.params.put(MessageParam.SCHEDULER, this.scheduler);
    }

    public Object invoke(Message message) {
        Logger.getInstance().debug(String.format("MessageBroker received: %s", message.toString()));
        HashMap<MessageParam, Object> parameters = message.getParameters();

        if (parameters == null)
            parameters = this.params;
        else
            parameters.putAll(this.params);

        return handlers.get(message.getAction()).execute(new Message(message.getAction(), parameters));
    }

    public Object invoke(MessageType op) {
        return this.invoke(new Message(op, this.params));
    }

    public Object invoke(MessageType op, HashMap<MessageParam, Object> parameters) {
        return this.invoke(new Message(op, parameters));
    }

    public Object invoke(MessageType op, Object parameters) {
        HashMap<MessageParam, Object> parametersMap = new HashMap<>();
        parametersMap.put(MessageParam.OTHERS, parameters);
        parametersMap.putAll(this.params);

        return this.invoke(new Message(op, parametersMap));
    }
}
