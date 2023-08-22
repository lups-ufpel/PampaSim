package simuladorso.MessageBroker;

import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Handlers.*;
import simuladorso.Utils.SimpleCommand;
import simuladorso.VirtualMachine.VirtualMachine;

import java.util.HashMap;

public class MessageBroker implements Runnable {
    private final Logger logger;
    private final HashMap<String, SimpleCommand> handlers = new HashMap<>();
    private final HashMap<String, Object> params = new HashMap<>();
    private static MessageBroker instance;
    private VirtualMachine vm;

    private MessageBroker() {
        this.logger = Logger.getInstance();

        handlers.put("GET_PROCESS_BY_PID", new GetProcessByPid(this.logger));
        handlers.put("LIST_PROCESSES_PIDS", new ListProcessPids(this.logger));
        handlers.put("LIST_NEW_PROCESSES", new ListNewProcesses(this.logger));
        handlers.put("LIST_READY_PROCESSES", new ListReadyProcesses(this.logger));
        handlers.put("LIST_RUNNING_PROCESSES", new ListRunningProcesses(this.logger));
        handlers.put("LIST_WAITING_PROCESSES", new ListWaitingProcesses(this.logger));
        handlers.put("LIST_TERMINATED_PROCESSES", new ListTerminatedProcesses(this.logger));
        handlers.put("START_VM", new StartVM(this.logger));
        handlers.put("STOP_VM", new StopVM(this.logger));
    }

    public static MessageBroker getInstance() {
        if (instance == null)
            instance = new MessageBroker();
        return instance;
    }

    @Override
    public void run() {

    }

    public Logger getLogger() {
        return logger;
    }

    public void setVm(VirtualMachine vm) {
        this.vm = vm;
        this.params.put("VM", this.vm);
    }



    public void handleMessage(Message message) {
        logger.debug(String.format("MessageBroker received: %s", message.toString()));

        message.setReceiver(vm);
        handlers.get(message.getAction()).execute(message);
    }
}
