package org.pampasim.resources;

import javafx.collections.ObservableList;
import lombok.experimental.StandardException;
import org.pampasim.core.Simulation;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.Map;

public interface ModuleSimulation extends Simulation {
    Class<?> configClass();
    void applyConfig(Object config) throws ConfigError;

    @StandardException
    class ConfigError extends Exception {}
}
