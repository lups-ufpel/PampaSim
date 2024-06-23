package org.pampasim;
import org.pampasim.Mediator.*;
import org.pampasim.Os.*;
import org.pampasim.VirtualMachine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main entry point for PampaSim application.
 * <p>
 *     Main functions:
 *     - Handle start of simulation entities.
 *     - Start the JavaFX main thread.
 * </p>
 */
public class Launcher {
    private static Logger LOGGER;
    private static final Mediator mediator = Mediator.getInstance();

    public static void main(String[] args) {
        configLogging(args);
        LOGGER.info("==================██Starting PampaOS Simulator██==================\n");
        initializeComponents();
        App.main(null);
    }

    private static void configLogging(String[] args) {
        //TODO: In the future include more logging options from command line arguments.
        LOGGER = LoggerFactory.getLogger(Launcher.class);
    }

    private static void initializeComponents() {
        mediator.registerComponent(new Os(mediator),Mediator.Component.KERNEL);
        mediator.registerComponent(new VmSimple(1,mediator),Mediator.Component.VM);
    }
}