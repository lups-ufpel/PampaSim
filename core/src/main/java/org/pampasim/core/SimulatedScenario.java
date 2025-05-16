package org.pampasim.core;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.core.dsl.SpecFileLexer;
import org.pampasim.core.dsl.SpecFileParser;
import org.pampasim.core.dsl.spec.Spec;
import org.pampasim.core.dsl.spec.SpecVisitor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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
