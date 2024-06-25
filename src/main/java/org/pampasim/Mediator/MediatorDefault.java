package org.pampasim.Mediator;

import javafx.scene.shape.Circle;
import org.pampasim.Os.MockOs;
import org.pampasim.Os.Scheduler;
import org.pampasim.gui.viewmodel.SimulationViewModel;
import org.pampasim.Mediator.Handlers.Core.*;
import org.pampasim.Mediator.Handlers.Process.*;
import org.pampasim.Mediator.Handlers.Scheduler.*;
import org.pampasim.Mediator.Handlers.Kernel.*;
import org.pampasim.Mediator.Handlers.VM.*;
import org.pampasim.Os.Process;
import org.pampasim.Utils.Command;
import org.pampasim.VirtualMachine.Vm;
import java.util.*;
public class MediatorDefault implements Mediator {
    private final Map<Circle, Process> circleProcessMap = new HashMap<>();
    private static final Mediator instance = new MediatorDefault();
    private final HashMap<Action, Command> handlers = new HashMap<>();
    private final HashMap<Component, Object> components = new HashMap<>();
    private final List<String> componentsName = new ArrayList<>();

    @Override
    public MockOs getOs() {
        return (MockOs) os;
        //todo: temporary solution
    }
    @Override
    public Scheduler getScheduler() {
        return (Scheduler) scheduler;
    }
    // MEDIATOR SHOULD HAVE A AN INSTANCE OF EACH COMPONENT TO HANDLE COMMUNICATION BETWEEN THEM
    private MockOs os;
    private Vm vm;
    private Scheduler scheduler;
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
                    this.os = (MockOs) component;
                    break;
                case VM:
                    this.vm = (Vm) component;
                    break;
                case SCHEDULER:
                    this.scheduler = (Scheduler) component;
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
                break;
            case EXECUTE:
                break;
            case RUN:
                vm.run();
                break;
            case ON_THIS_PROCESS_DISPATCHED:
                break;
            case ON_THIS_PROCESS_INTERRUPTED:
                break;
            case ON_THIS_PROCESS_SUBMITTED:
                break;
            case ON_THIS_PROCESS_FINISHED:
                break;
            case VISUALIZE:
                break;
            case ON_THIS_PROCESS_EXECUTED:
                double progress = ((Process)object).getProgress();
            case DISPATCH_ALL:
                break;
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
