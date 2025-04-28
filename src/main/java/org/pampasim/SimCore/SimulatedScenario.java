package org.pampasim.SimCore;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.SimEntity.ProcessManager;
import org.pampasim.SimEntity.Processor;
import org.pampasim.SimEntity.Scheduler;
import org.pampasim.dsl.EntityDSLLexer;
import org.pampasim.dsl.SpecFileLexer;
import org.pampasim.dsl.SpecFileParser;
import org.pampasim.dsl.spec.Spec;
import org.pampasim.dsl.spec.SpecVisitor;

import java.io.IOException;
import java.util.List;

@Getter
@Setter
public class SimulatedScenario {

    public List<Processor> processors;
    public Scheduler scheduler;
    public ProcessManager processManager;
    public final Simulation simulation;
    @Getter
    private Spec spec = null;

    public SimulatedScenario() {
        this.simulation = new PampaSimWithTrace();
        String fileName = "spec.spec";
        CharStream stream = null;
        try {
            stream = CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            System.err.println(fileName + " not found!");
            return;
        }
        var lexer = new SpecFileLexer(stream);
        var tokStream = new CommonTokenStream(lexer);
        var specParser = new SpecFileParser(tokStream);
        var specVisitor = new SpecVisitor();
        this.spec = specVisitor.visit(specParser.specFile());
        // alrighty, this is the happy path, but the spec is just chilling here, no telling when it'll be used
        // and the interface still doesn't know jack about the current state of the events.
    }
}
