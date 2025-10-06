package org.pampasim;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.Simulation;
import org.pampasim.dsl.spec.Spec;

import java.net.URL;
import java.nio.file.Path;
import java.util.function.Function;

@Getter
public class SimulatedScenario {
    private Simulation simulation;
    @Setter
    private Function<Spec, Simulation> simulationFactory;

    @Setter
    private Spec spec;
    @Setter
    private boolean saved = true; // an empty scenario is "saved" since it doesn't need saving

    // feels not very java-y but by jove I love passing functions around
    public SimulatedScenario(Spec template, Function<Spec, Simulation> simulationFactory) {
        this.spec = template;
        this.simulationFactory = simulationFactory;
        this.simulation = simulationFactory.apply(template);
    }

    public void resetToSpec() {
        this.simulation = simulationFactory.apply(this.spec);
    }

    public void saveSpec(Path path) {
        this.spec.saveSpec(path);
        this.saved = true;
    }
}
