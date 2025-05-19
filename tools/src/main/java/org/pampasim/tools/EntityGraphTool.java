package org.pampasim.tools;

import guru.nidi.graphviz.attribute.*;
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
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public class EntityGraphTool {

    public static void main(String[] args) throws IOException {
        Map<String, Event> allEvents = new HashMap<>();
        Map<String, Entity> allEntities = new HashMap<>();
        Map<String, EventGroup> allEventGroups = new HashMap<>();
        Map<Event, Set<Entity>> eventDestinations = new HashMap<>();
        Map<Event, Set<Entity>> eventSources = new HashMap<>();
        Map<Set<EventGroup>, Integer> dataColorMap = new HashMap<>();

        for (String path : args) {
            CharStream stream = null;
            try {
                stream = CharStreams.fromFileName(path);
            } catch (IOException e) {
                System.err.println(path + " not found!");
                assert (false);
            }
            var lexer = new EntityDSLLexer(stream);
            var tokenStream = new CommonTokenStream(lexer);
            var parser = new EntityDSLParser(tokenStream);
            parser.setBuildParseTree(true);
            EntityDSLParser.DescriptionFileContext tree
                    = parser.descriptionFile();
            allEntities.putAll(parser.getEntities());
            allEvents.putAll(parser.getEvents());
            allEventGroups.putAll(parser.getEventGroups());
        }

        System.out.println(allEntities);
        System.out.println(allEvents);

        Set<Set<String>> mergeables = new HashSet<>();
        for (var ent : allEntities.values()) {
            Set<EventGroup> acceptedGroups = ent.allAcceptedEvents()
                    .stream().map(event -> allEventGroups.get(event.getGroupName()))
                    .collect(Collectors.toSet())
                    ;
            dataColorMap.computeIfAbsent(acceptedGroups,_key -> dataColorMap.size()+1);

            for (var otherEnt : allEntities.values()) {
                if (ent == otherEnt) continue;
                boolean isSuperHandler = true;
                for (var event : otherEnt.getHandlers().keySet()) {
                    if (ent.getHandlers().get(event) == null) {
                        isSuperHandler = false;
                        break;
                    }
                }
                if (isSuperHandler) {
                    mergeables.add(Set.of(ent.getName(), otherEnt.getName()));
                }
            }
        }

        System.out.println("mergeables are " + mergeables);

        for (var set : mergeables) {
            var inOrder = set.stream()
                    .map(allEntities::get)
                    .sorted(Comparator.comparingInt(e -> e.getHandlers().size()))
                    .toList();
            var smallest = inOrder.getFirst();
            allEntities.remove(smallest.getName());
        }

        allEvents.values().forEach(event -> {
            eventSources.computeIfAbsent(event, k -> new HashSet<>());
            eventDestinations.computeIfAbsent(event, k -> new HashSet<>());
        });

        Map<Entity, Node> entityNodes = new HashMap<>();

        for (Entity e : allEntities.values()) {
            Set<EventGroup> acceptedGroups = e.allAcceptedEvents()
                    .stream().map(event -> allEventGroups.get(event.getGroupName()))
                    .collect(Collectors.toSet())
                    ;
            Node entNode = node(e.getName())
                    .with(Attributes.attr("height", e.getHandlers().size()))
                    .with(Attributes.attr("color", dataColorMap.get(acceptedGroups)));
            entityNodes.put(e, entNode);
            for (Handler h : e.getHandlers().values()) {
                System.out.println(h);
                { // Add to destinations
                    Event event = h.event();
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
        Graph entGraph = Factory.graph("Entity graph")
                .directed()
                .with(entityNodes.values().stream().toList())
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .graphAttr().with(GraphAttr.splines(GraphAttr.SplineMode.SPLINE))
                .nodeAttr().with(Attributes.attr("shape", "box"))
                .nodeAttr().with(Attributes.attr("style", "filled"))
                .nodeAttr().with(Attributes.attr("colorscheme", "pastel28"))
                .nodeAttr().with(Attributes.attr("fontsize", 24))
                .linkAttr().with(Attributes.attr("fontsize", 12))
                .linkAttr().with(Attributes.attr("decorate", true));

        for (Event event : allEvents.values()) {
            for (Entity src : eventSources.get(event)) {
                Node srcNode = entityNodes.get(src);
                assert(srcNode != null);
                for (Entity dst : eventDestinations.get(event)) {
                    Node dstNode = entityNodes.get(dst);
                    entGraph = entGraph.with(
                            srcNode.link(to(dstNode)
                                    .with(Label.of(event.getName()
                                            .replace(".", "\n")
                                    )))
                    );
                    System.out.println("link " + src.getName() + " -> " + dst.getName());
                }
            }
        }

        var viz = Graphviz.fromGraph(entGraph);
        viz.engine(Engine.DOT).render(Format.SVG).toFile(new File("entity-network.dot.svg"));
        viz.render(Format.DOT).toFile(new File("entity-network.dot"));
    }
}
