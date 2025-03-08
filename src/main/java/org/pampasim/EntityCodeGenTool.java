package org.pampasim;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.dsl.EntityDSLLexer;
import org.pampasim.dsl.EntityDSLParser;
import org.pampasim.dsl.metadata.Entity;
import org.pampasim.dsl.metadata.Event;
import org.pampasim.dsl.metadata.Handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static guru.nidi.graphviz.model.Factory.*;

/// Generates a specialized EventManager for a given
/// description file, along with the Entities used within,
/// using accompanying EntityImpl classes
public class EntityCodeGenTool {
    protected String implCodePath = "src/main/java/org/pampasim/SimEntityImpls";

    public record CodeSections (String eventElements, String eventRegistration, String processEvent) {
        public String patchClass(String classCode) {
            String acc = classCode;
            acc = acc.replaceFirst("//\s*codegen\s+events\s*\n", eventElements);
            acc = acc.replaceFirst("//\s*codegen\s+register\s+handlers\s*\n", eventRegistration);
            acc = acc.replaceFirst("//\s*codegen\s+processEvent\s*\n", processEvent);
            return acc;
        }
    };

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

        Map<Event, Set<Entity>> eventDestinations = new HashMap<>();
        Map<Event, Set<Entity>> eventSources = new HashMap<>();
        parser.getEvents().forEach(event -> {
            eventSources.computeIfAbsent(event, k -> new HashSet<>());
            eventDestinations.computeIfAbsent(event, k -> new HashSet<>());
        });

        Map<Entity, Node> entityNodes = new HashMap<>();

        for (Entity e : parser.getEntities().values()) {
            Node entNode = node(e.getName());
            entityNodes.put(e, entNode);
            for (Handler h : e.getHandlers().values()) {
                System.out.println(h);
                { // Add to destinations
                    Event event = h.eventStatePair().getEvent();
                    Set<Entity> dstSet = eventDestinations.get(event);
                    dstSet.add(e);
                }
                { // Add to sources
                    for (Event event : h.chainedEvents()) {
                        Set<Entity> srcSet = eventSources.get(event);
                        srcSet.add(e);
                    }
                }
            }
        }


        System.out.println("srcs " + eventSources);
        System.out.println("dsts " + eventDestinations);
        Graph entGraph = graph("Entity graph").directed()
                .with(entityNodes.values().stream().toList());

        for (Event event : parser.getEvents()) {
            for (Entity src : eventSources.get(event)) {
                Node srcNode = entityNodes.get(src);
                assert(srcNode != null);
                for (Entity dst : eventDestinations.get(event)) {
                    Node dstNode = entityNodes.get(dst); //.with(linkAttrs()));
                    entGraph = entGraph.with(
                            srcNode.link(to(dstNode).with(Label.of(event.name())))
                    );
                    System.out.println("link " + src.getName() + " -> " + dst.getName());
                }
            }
        }

        var viz = Graphviz.fromGraph(entGraph);
        viz.render(Format.SVG).toFile(new File("entity-network.svg"));
        viz.render(Format.DOT).toFile(new File("entity-network.dot"));
    }
}
