package org.pampasim.core;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.core.entity.Processor;
import org.pampasim.core.dsl.SpecFileLexer;
import org.pampasim.core.dsl.SpecFileParser;
import org.pampasim.core.dsl.spec.Spec;
import org.pampasim.core.dsl.spec.SpecVisitor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class SimulatedScenario {
    @Getter
    public Simulation simulation;

    @Getter
    private Spec loadedSpec; // differs from spec when manual modifications are made

    @Getter
    @Setter
    private Spec spec;

    public SimulatedScenario() {
        this.simulation = new PampaSim(null);
        this.spec = new Spec();
    }

    public void loadSpec(URL url) {
        try {
            URLConnection conn = url.openConnection();
            BufferedInputStream bufStream = new BufferedInputStream(conn.getInputStream());
            CharStream stream = CharStreams.fromStream(bufStream);
            var lexer = new SpecFileLexer(stream);
            var tokStream = new CommonTokenStream(lexer);
            var specParser = new SpecFileParser(tokStream);
            var specVisitor = new SpecVisitor();
            this.loadedSpec = specVisitor.visitSpecFile(specParser.specFile());
            this.spec = loadedSpec;
            resetToSpec();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetToSpec() {
        this.simulation = new PampaSim(null);
        this.simulation.applySpec(getSpec());
    }
}
