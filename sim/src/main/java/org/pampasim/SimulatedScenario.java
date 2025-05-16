package org.pampasim;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.Simulation;
import org.pampasim.dsl.spec.Spec;

import java.util.function.Function;

public class SimulatedScenario {
    @Getter
    private Simulation simulation;
    @Getter
    @Setter
    private Function<Spec, Simulation> simulationFactory;

    @Getter
    @Setter
    private Spec spec;

    // feels not very java-y but by jove I love passing functions around
    public SimulatedScenario(Spec template, Function<Spec, Simulation> simulationFactory) {
        this.spec = template;
        this.simulationFactory = simulationFactory;
        this.simulation = simulationFactory.apply(template);
    }


    public void resetToSpec() {
        this.simulation = simulationFactory.apply(this.spec);
    }
}
