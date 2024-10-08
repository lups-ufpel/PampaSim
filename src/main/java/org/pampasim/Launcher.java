package org.pampasim;

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

    public static void main(String[] args) {
        configLogging(args);
        LOGGER.info("==================██Starting PampaOS Simulator██==================\n");
        PampaSimGUI.launch(PampaSimGUI.class,args);
    }

    private static void configLogging(String[] args) {
        //TODO: In the future include more logging options from command line arguments.
        // such as enabling a special debug logging mode for debugging from args.
        // tweak some logging configuration properties to improve performance and legibility.
        // add the option for redirecting all or some log messages to a file.
        LOGGER = LoggerFactory.getLogger(Launcher.class);
    }
}