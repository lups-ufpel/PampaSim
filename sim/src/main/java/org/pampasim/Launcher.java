package org.pampasim;

import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;

/**
 * The main entry point for PampaSim application.
 * <p>
 *     Main functions:
 *     - Handle start of simulation entities.
 *     - Start the JavaFX main thread.
 * </p>
 */
@Command(name="PampaSim", mixinStandardHelpOptions = true)
public class Launcher implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(Launcher.class);
    private static String[] args;

    @Getter
    @Option(names = {"-s", "--spec"},
            paramLabel = "<ARQUIVO-SPEC>",
            description = "especificação a ser carregada")
    private static Path autoloadSpec = null;

    @Getter
    @Option(names = {"-l", "--log-level"},
            paramLabel = "<OFF|FATAL|ERROR|WARN|[INFO]|DEBUG|TRACE|ALL>",
            description = "nível de verbosidade dos logs")
    private static Level debugLevel = Level.INFO;

    @Override
    public void run() {
        LOGGER.info("==================██Starting PampaOS Simulator██==================\n");
        PampaSimGUI.launch(PampaSimGUI.class,args);
    }

    public static void main(String[] args) {
        Launcher.args = args;
        var cmdline = new CommandLine(new Launcher());
        cmdline.registerConverter(Level.class, new Log4jLevelConverter());
        int exitCode = cmdline.execute(args);
        System.exit(exitCode);
    }

    public static class Log4jLevelConverter implements CommandLine.ITypeConverter<Level> {
        @Override
        public Level convert(String value) throws Exception {
            return Level.valueOf(value.toUpperCase());
        }
    }
}