package org.pampasim;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import org.pampasim.dsl.EntityDSLLexer;
import org.pampasim.dsl.EntityDSLParser;
import org.pampasim.dsl.metadata.*;

import java.io.IOException;
import java.io.File;

import static guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;

public class EntityGraphTool {
    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        CharStream stream = null;
        try {
            stream = CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            System.err.println(fileName + " not found!");
            assert (false);
        }
        var lexer = new EntityDSLLexer(stream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new EntityDSLParser(tokenStream);
        parser.setBuildParseTree(true);
        EntityDSLParser.DescriptionFileContext tree
                = parser.descriptionFile();
        System.out.println(parser.getEvents());
        System.out.println(parser.getEntities());
        Graph entGraph = graph("Entity graph");

        for (Entity e : parser.getEntities().values()) {
            entGraph = entGraph.with(node(e.getName()));
        }

        Graphviz.fromGraph(entGraph)
                .render(Format.SVG)
                .toFile(new File("entity-network.svg"));
    }
}
