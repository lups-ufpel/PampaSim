package org.simuladorso;
import org.simuladorso.Mediator.*;
import org.simuladorso.Os.*;
import org.simuladorso.VirtualMachine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static final Logger LOGGER = LoggerFactory.getLogger("Main");
    private static final Mediator mediator = Mediator.getInstance();

    public static void main(String[] args) {
        LOGGER.info("==================██Starting PampaOS Simulator██==================\n");
        initializeComponents();
        MainApp.main(null);
    }
    private static void initializeComponents() {
        mediator.registerComponent(new Os(mediator),Mediator.Component.KERNEL);
        mediator.registerComponent(new VmSimple(1,mediator),Mediator.Component.VM);
    }
}


