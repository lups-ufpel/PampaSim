package org.simuladorso.Mediator;


import javafx.application.Platform;
import org.simuladorso.Mediator.Handlers.Core.*;
import org.simuladorso.Mediator.Handlers.Process.*;
import org.simuladorso.Mediator.Handlers.Scheduler.*;
import org.simuladorso.Mediator.Handlers.Kernel.*;
import org.simuladorso.Mediator.Handlers.VM.*;
import org.simuladorso.Utils.Command;

import java.util.*;
import java.util.function.Consumer;

public class MediatorDefault implements Runnable, Mediator {


    private final HashMap<Action, Command> handlers = new HashMap<>();
    private final HashMap<Component, Object> components = new HashMap<>();
    private final List<String> componentsName = new ArrayList<>();

    private final Map<Action, List<SubscriberObject>> subscribers = new LinkedHashMap<>();
    private static final MediatorDefault instance = new MediatorDefault();
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
        //handlers.put(Action.UPDATE_CORES_INFO, new UpdateCoresInfo());

    }

    @Override
    public void run() {

    }
    @Override
    public void registerComponent(Component componentType, Object component) {
        // Handle when ComponentType is Missing but component is valid.
        if (componentType != null && component != null) {
            this.components.put(componentType, component);
            LOGGER.info("Component of {} and type of {} has been successfully registered", component.getClass().getSimpleName(), componentType);
            componentsName.add(component.getClass().getSimpleName());
        }
        else{
            LOGGER.error("REGISTERING A COMPONENT WITH NULL TYPE OR OBJECT PARAMS: [{} {}]",componentType,component);
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

    public String[][] getComponentsNames(){
        String[][] twoDimArray = new String[componentsName.size()][1];

        for (int i = 0; i < componentsName.size(); i++) {
            twoDimArray[i][0] = componentsName.get(i);
        }
        return twoDimArray;
    }
    public void publish(Action action) {
        Platform.runLater(() ->{
            List<SubscriberObject> subscriberList = instance.subscribers.get(action);
            if(subscriberList != null){
                subscriberList.forEach(subscriberObject -> subscriberObject.getCb().accept(action));
            }

        });
    }
    public void subscribe(Action event, Object subscriber, Consumer<Action> cb){
        if (!instance.subscribers.containsKey(event)) {
            List<SubscriberObject> slist = new ArrayList<>();
            instance.subscribers.put(event,slist);
        }
        List<SubscriberObject> subscriberList = instance.subscribers.get(event);
        subscriberList.add(new SubscriberObject(subscriber,cb));
    }
    static class SubscriberObject {

        private final Object subscriber;
        private final Consumer<Action> cb;

        public SubscriberObject(Object subscriber, Consumer<Mediator.Action> cb){
            this.subscriber = subscriber;
            this.cb = cb;
        }
        public Object getSubscriber(){
            return subscriber;
        }
        public Consumer<Mediator.Action> getCb() {
            return cb;
        }
    }
}
