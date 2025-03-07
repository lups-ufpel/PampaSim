package org.pampasim;

import guru.nidi.graphviz.model.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import org.pampasim.dsl.EntityDSLLexer;
import org.pampasim.dsl.EntityDSLParser;
import org.pampasim.dsl.metadata.*;

import java.io.IOException;
import java.io.File;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

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

        Map<String, Set<Entity>> eventDestinations = new HashMap<>();
        Map<String, Set<Entity>> eventSources = new HashMap<>();
        parser.getEvents().forEach(eventName -> {
            eventSources.computeIfAbsent(eventName, k -> new HashSet<>());
            eventDestinations.computeIfAbsent(eventName, k -> new HashSet<>());
        });

        Map<Entity, Node> entityNodes = new HashMap<>();

        for (Entity e : parser.getEntities().values()) {
            Node entNode = node(e.getName());
            entityNodes.put(e, entNode);
            for (Handler h : e.getHandlers().values()) {
                { // Add to destinations
                    String eventName = h.eventStatePair().getEventName();
                    Set<Entity> dstSet = eventDestinations.get(eventName);
                    dstSet.add(e);
                }
                { // Add to sources
                    for (String eventName : h.chainedEvents()) {
                        Set<Entity> srcSet = eventSources.get(eventName);
                        srcSet.add(e);
                    }
                }
            }
        }


        System.out.println("srcs " + eventSources);
        System.out.println("dsts " + eventDestinations);
        Graph entGraph = graph("Entity graph").directed()
                .with(entityNodes.values().stream().toList());

        for (String eventName : parser.getEvents()) {
            for (Entity src : eventSources.get(eventName)) {
                Node srcNode = entityNodes.get(src);
                assert(srcNode != null);
                for (Entity dst : eventDestinations.get(eventName)) {
                    Node dstNode = entityNodes.get(dst); //.with(linkAttrs()));
                    entGraph = entGraph.with(srcNode.link(dstNode));
                    System.out.println("link " + src.getName() + " -> " + dst.getName());
                }
            }
        }

        Graphviz.fromGraph(entGraph)
                .render(Format.SVG)
                .toFile(new File("entity-network.svg"));
        Graphviz.fromGraph(entGraph)
                .render(Format.DOT)
                .toFile(new File("entity-network.dot"));
    }
}
