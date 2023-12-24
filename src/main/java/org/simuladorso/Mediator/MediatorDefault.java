package org.simuladorso.Mediator;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.simuladorso.GUI.SimulationViewModel;
import org.simuladorso.Mediator.Handlers.Core.*;
import org.simuladorso.Mediator.Handlers.Process.*;
import org.simuladorso.Mediator.Handlers.Scheduler.*;
import org.simuladorso.Mediator.Handlers.Kernel.*;
import org.simuladorso.Mediator.Handlers.VM.*;
import org.simuladorso.Os.Os;
import org.simuladorso.Os.Process;
import org.simuladorso.Utils.Command;
import org.simuladorso.VirtualMachine.Vm;
import java.util.*;
public class MediatorDefault implements Mediator {
    private final Map<Circle, Process> circleProcessMap = new HashMap<>();
    private static final Mediator instance = new MediatorDefault();
    private final HashMap<Action, Command> handlers = new HashMap<>();
    private final HashMap<Component, Object> components = new HashMap<>();
    private final List<String> componentsName = new ArrayList<>();

    // MEDIATOR SHOULD HAVE A AN INSTANCE OF EACH COMPONENT TO HANDLE COMMUNICATION BETWEEN THEM
    private Os os;
    private Vm vm;
    private SimulationViewModel simulationViewModel;

    public Object retrieveComponent(Component componentType) {
        return this.components.get(componentType);
    }

    /*
     * This is the constructor of the MediatorDefault class.
     * It is private because we are using the Singleton pattern.
     * It initializes the handlers HashMap with the different actions
     * and their respective handlers.
     */
    public MediatorDefault() {
        handlers.put(Action.GET_SIM_STATUS, new sim_status());
        handlers.put(Action.LIST_PROCESSES_PIDS, new ListProcessesPids());
        handlers.put(Action.LIST_NEW_PROCESSES, new ListNewProcesses());
        handlers.put(Action.LIST_READY_PROCESSES, new ListReadyProcesses());
        handlers.put(Action.LIST_WAITING_PROCESSES, new ListWaitingProcesses());
        handlers.put(Action.LIST_TERMINATED_PROCESSES, new ListTerminatedProcesses());
        handlers.put(Action.START_VM, new StartVM());
        handlers.put(Action.STOP_VM, new StopVM());
        handlers.put(Action.SCHEDULER_SCHEDULE, new Schedule());
        handlers.put(Action.SCHEDULER_ADD_TO_QUEUE,new SchedulerAddToQueue());
        handlers.put(Action.KERNEL_NEW_PROCESS, new NewProcess());
        handlers.put(Action.CORE_EXECUTE, new Execute());
        handlers.put(Action.LIST_CORES, new ListCores());
        handlers.put(Action.PROCESS_GET_PID, new GetPid());
        handlers.put(Action.GET_TIME, new GetTick());
    }

    public static Mediator getInstance(){
        return instance;
    }
    @Override
    public void registerComponent(Object component,Component componentType) {
        // Handle when ComponentType is Missing but component is valid.
        if (componentType != null && component != null) {
            this.components.put(componentType, component);
            LOGGER.info("Component of {} and type of {} has been successfully registered", component.getClass().getSimpleName(), componentType);
            switch(componentType){
                case GUI:
                    this.simulationViewModel = (SimulationViewModel) component;
                    break;
                case KERNEL:
                    this.os = (Os) component;
                    break;
                case VM:
                    this.vm = (Vm) component;
                    break;
            }
        }
        else{
            LOGGER.error("REGISTERING A COMPONENT WITH NULL TYPE OR OBJECT PARAMS: [{} {}]",componentType,component);
        }
    }
    @Override
    public void send(Object object, Action action) {
        switch (action) {
            case CREATE:
                // compare object to simulationViewModel
                if (object == this.simulationViewModel){
                    int pid = simulationViewModel.burstProperty().get();
                    int priority = simulationViewModel.priorityProperty().get();
                    int arrivalTime = simulationViewModel.arrivalTimeProperty().get();
                    Process newProcess = os.createProcess(Process.Type.SIMPLE, pid, priority, arrivalTime);
                    simulationViewModel.AddProcess(newProcess);
                } else{
                    LOGGER.error("Object passed to send() does not match the specific Action");
                }
                break;
            case EXECUTE:
                //Process proc = os.requeueProcess();
                //((Vm)object).setProcess(proc);
                break;
            case RUN:
                vm.run();
                break;
            case ON_THIS_PROCESS_DISPATCHED:
                simulationViewModel.dispatchProcess((Process) object);
                break;
            case ON_THIS_PROCESS_INTERRUPTED:
                simulationViewModel.interruptProcess((Process) object);
                break;
            case ON_THIS_PROCESS_SUBMITTED:
                simulationViewModel.submitProcess((Process) object);
                break;
            case ON_THIS_PROCESS_FINISHED:
                simulationViewModel.finishProcess((Process) object);
                break;
            case VISUALIZE:
                Circle circle = (Circle) object;
                Process process = circleProcessMap.get(circle);
                Color col = (Color) circle.fillProperty().get();
                //simulationViewModel.showProcessInfo(process,col);
                break;
            case ON_THIS_PROCESS_EXECUTED:
                double progress = ((Process)object).getProgress();
                simulationViewModel.updateProcessProgress((Process)object,progress);


        }
    }


    @Override
    public Object invoke(Message message) {

        LOGGER.debug("MessageBroker received: {}", message.toString());
        //Logger.getInstance().debug(String.format("MessageBroker received: %s", message.toString()));

        Command executor = handlers.get(message.getAction());

        if (executor == null) {
            LOGGER.error("Message action passed not implemented: {}",message.getAction());
            //Logger.getInstance().error("Message action passed not implemented: " + message.getAction());
        }

        message.setComponents(this.components);

        return handlers.get(message.getAction()).execute(message);
    }
    @Override
    public Object invoke(Action action) {
        return this.invoke(new Message(action, null));
    }

    @Override
    public Object invoke(Action action, Object[] parameters) {
        return this.invoke(new Message(action, parameters));
    }
}
