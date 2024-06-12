package org.pampasim;
import org.pampasim.Mediator.*;
import org.pampasim.Os.*;
import org.pampasim.VirtualMachine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Mediator mediator = Mediator.getInstance();

    public static void main(String[] args) {
        LOGGER.info("==================██Starting PampaOS Simulator██==================\n");
        initializeComponents();
        App.main(null);
    }
    private static void initializeComponents() {
        mediator.registerComponent(new Os(mediator),Mediator.Component.KERNEL);
        mediator.registerComponent(new VmSimple(1,mediator),Mediator.Component.VM);
    }
}