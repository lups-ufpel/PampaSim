package org.pampasim.Utils;

import guru.nidi.graphviz.model.Graph;

public interface GraphVisualizeable {
    public Graph exportGraph();
    public String graphNodeName();
}
