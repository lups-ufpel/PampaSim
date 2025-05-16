package org.pampasim.tools;

import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.model.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import org.pampasim.core.dsl.EntityDSLLexer;
import org.pampasim.core.dsl.EntityDSLParser;
import org.pampasim.core.dsl.metadata.*;

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

        Map<Event, Set<Entity>> eventDestinations = new HashMap<>();
        Map<Event, Set<Entity>> eventSources = new HashMap<>();
        parser.getEvents().values().forEach(event -> {
            eventSources.computeIfAbsent(event, k -> new HashSet<>());
            eventDestinations.computeIfAbsent(event, k -> new HashSet<>());
        });

        Map<Entity, Node> entityNodes = new HashMap<>();

        for (Entity e : parser.getEntities().values()) {
            Node entNode = Factory.node(e.getName());
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
        Graph entGraph = Factory.graph("Entity graph").directed()
                .with(entityNodes.values().stream().toList())
                .nodeAttr().with(Attributes.attr("fontsize", 24))
                .linkAttr().with(Attributes.attr("fontsize", 12), Attributes.attr("len", 4));

        for (Event event : parser.getEvents().values()) {
            for (Entity src : eventSources.get(event)) {
                Node srcNode = entityNodes.get(src);
                assert(srcNode != null);
                for (Entity dst : eventDestinations.get(event)) {
                    Node dstNode = entityNodes.get(dst); //.with(linkAttrs()));
                    entGraph = entGraph.with(
                            srcNode.link(Factory.to(dstNode).with(Label.of(event.getName())))
                    );
                    System.out.println("link " + src.getName() + " -> " + dst.getName());
                }
            }
        }

        var viz = Graphviz.fromGraph(entGraph);
        viz.engine(Engine.DOT).render(Format.SVG).toFile(new File("entity-network.dot.svg"));
        viz.engine(Engine.CIRCO).render(Format.SVG).toFile(new File("entity-network.circo.svg"));
        viz.engine(Engine.FDP).render(Format.SVG).toFile(new File("entity-network.fdp.svg"));
        viz.engine(Engine.NEATO).render(Format.SVG).toFile(new File("entity-network.neato.svg"));
        viz.render(Format.DOT).toFile(new File("entity-network.dot"));
    }
}
