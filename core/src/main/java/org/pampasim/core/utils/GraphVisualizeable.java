package org.pampasim.core.utils;

import guru.nidi.graphviz.model.Graph;

public interface GraphVisualizeable {
    public Graph exportGraph();
    public String graphNodeName();
}
