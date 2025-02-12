package org.pampasim.SimCore;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Compass;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.Node;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.Utils.GraphVisualizeable;

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
    private List<PampaSimEvent> currentClockEvents = null;
    @Override
    public boolean runClockAndProcessEvents() {
        currentClockEvents = null;
        executeRunnableEntities();
        // the point of it all
        currentClockEvents = future.stream().collect(Collectors.toList());
        if(future.isEmpty()) {
            return false;
        } else {
            final PampaSimEvent first = future.first();
            clock = first.delay();
            processEvent(first);
            future.remove(first);
            return true;
        }
    }

    @Override
    public Graph exportGraph() {
        String name = this.getClass().getSimpleName();
        boolean noEvents = currentClockEvents == null || currentClockEvents.isEmpty();
        String bufferTable= "<table border='0' cellborder='1' cellspacing='0'>\n" +
                (noEvents? "<tr><td>empty</td></tr>\n" :
                        currentClockEvents.stream()
                                .collect(StringBuilder::new,
                                        (acc, elem) ->
                                                acc.append("<tr><td port=\"portev")
                                                        .append(elem.getSerial())
                                                        .append("\">")
                                                        .append(elem)
                                                        .append("</td></tr>\n"),
                                                //acc.append("<tr><td>").append(elem).append("</td></tr>"),
                                        StringBuilder::append).toString()
                ) +
                "</table>\n";
        String htmlTable = "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td>" + name + "</td><td>Clock " + this.getClock() + "</td></tr>\n" +
                "<tr><td colspan='2'>" + bufferTable + "</td></tr>\n" +
                "</table>\n";
        Node root = node(graphNodeName())
                .with(Shape.PLAIN_TEXT)
                .with(Label.html(htmlTable));
        Graph g = graph(name)
                .directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT));
        // this probably needs to use the mutable graph api, to avoid too many allocs
        var entityGraphMap = this.entityList.stream().collect(
                () -> new HashMap<String, Graph>(),
                (map, entity) -> map.put(entity.graphNodeName(), entity.exportGraph()),
                HashMap::putAll);
        for(var subgraph : entityGraphMap.values()) {
            g = g.with(subgraph);
        }
        if (!noEvents) {
            for (PampaSimEvent ev : this.currentClockEvents) {
                g = g.with(
                        root.link(
                                between(
                                        port("portev" + ev.getSerial()),
                                        entityGraphMap.get(ev.getDestination().graphNodeName())
                                )
                        ),
                        entityGraphMap.get(ev.getSource().graphNodeName()).link(root)
                );
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
