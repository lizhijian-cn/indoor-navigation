package org.wangpai.navigation.core.edge;

import lombok.Builder;
import org.wangpai.navigation.core.vertex.VertexKey;

@Builder
public class Edge {
    public int next;
    public VertexKey to;
    public double weight;
}
