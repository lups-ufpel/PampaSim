package org.pampasim.SimCore;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Compass;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.Node;
import org.pampasim.SimCore.events.Event;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.Utils.GraphVisualizeable;
import org.pampasim.dsl.spec.Spec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

/* This whole class is dubious, it only exists because I couldn't think of another
 * way to get in between the runnable entities execution step, which populates the
 * future queue and the event processing step, which pops them off.
 * I did modify some member access qualifiers to make this botch work:
 * future: private -> protected
 * executeRunnableEntities() -> private -> protected
 * clock: private -> protected
 * entityList: private -> protected
 */
public class PampaSimWithTrace extends PampaSim implements GraphVisualizeable {
    private final FutureQueue currentClockEvents = new FutureQueue();

    public PampaSimWithTrace() { super(); }
    public PampaSimWithTrace(Spec spec) {
        super(spec);
    }

    @Override
    public boolean runClockAndProcessEvents() {
        // Had to mod FutureQueue to expose this method
        currentClockEvents.clear();
        //System.out.println("Cleared current clock events!");
        return super.runClockAndProcessEvents();
    }

//    @Override
//    protected void processEvent(PampaSimEvent evt) {
//        super.processEvent(evt);
//        System.out.println("Got ev: " + evt);
//        currentClockEvents.addEvent(evt);
//        System.out.println("Current sim clock (" + getSimulationClock() + ") events:");
//        currentClockEvents.stream().forEach((e) -> System.out.println("\t" + e));
//    }

    @Override
    public Graph exportGraph() {
        String name = this.getClass().getSimpleName();
        boolean noEvents = currentClockEvents.isEmpty();
        String bufferTable= "<table border='0' cellborder='1' cellspacing='0'>\n" +
                (noEvents? "<tr><td>empty</td></tr>\n" :
                        currentClockEvents.stream()
                                .collect(StringBuilder::new,
                                        (acc, elem) ->
                                                acc.append("<tr><td port=\"ev")
                                                   .append(elem.getSerial())
                                                   .append("\">")
                                                   .append(elem)
                                                   .append("</td></tr>\n"),
                                        StringBuilder::append).toString()
                ) +
                "</table>\n";
        String htmlTable = "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td>" + name + "</td><td>Clock " + this.getSimulationClock() + "</td></tr>\n" +
                "<tr><td colspan='2' cellborder='0'>" + bufferTable + "</td></tr>\n" +
                "</table>\n";
        Node root = node(graphNodeName())
                .with(Shape.PLAIN_TEXT)
                .with(Label.html(htmlTable));
        Graph g = graph(name)
                .directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .with(root);
        // this probably needs to use the mutable graph api, to avoid too many allocs
        var entityGraphMap = this.entityList.stream().collect(
                () -> new HashMap<String, Graph>(),
                (map, entity) -> map.put(entity.graphNodeName(), entity.exportGraph()),
                HashMap::putAll);
        for(var subgraph : entityGraphMap.values()) {
            g = g.with(subgraph);
            g = g.with(root.link(to(subgraph).with(Style.INVIS)));
        }
        if (!noEvents) {
            for (Event ev : this.currentClockEvents.stream().toList()) {
                List<PampaSimEntity> dsts = this.getEventManager().getAllDestinations(ev.getClass());
                for (var dstEntity : dsts) {
                    g = g.with(
                            root.link(
                                    between(
                                            port("ev" + ev.getSerial()),
                                            entityGraphMap.get(dstEntity.graphNodeName())
                                    )
                            ),
                            entityGraphMap.get(ev.getSource().graphNodeName()).link(root)
                    );
                }
            }
        }
        return g;
    }

    @Override
    public String graphNodeName() {
        // WARN / FIXME: will name conflict if there are multiple entities of the same type in a simulation!
        // I'll let it be for now, since that edge case is very unlikely
        // ...this being the even more special case as the graph root
        return this.getClass().getSimpleName();
    }
}
