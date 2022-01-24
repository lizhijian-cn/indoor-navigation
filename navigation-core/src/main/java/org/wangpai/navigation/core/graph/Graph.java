package org.wangpai.navigation.core.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangpai.navigation.core.edge.Edge;
import org.wangpai.navigation.core.vertex.Vertex;
import org.wangpai.navigation.core.vertex.VertexKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 三维图
 */
public class Graph {
    //    List<Layout> layoutList;
    private static Logger logger = LoggerFactory.getLogger(Graph.class);

    List<Vertex> vertexList;
    Map<VertexKey, Vertex> vertexMap = new HashMap<>();
    List<Edge> edgeList = new ArrayList<>();
    Map<VertexKey, Integer> head = new HashMap<>();

    public Graph(List<Vertex> vertexList) {
        this.vertexList = vertexList;
        for (Vertex vertex : vertexList) {
            vertexMap.put(vertex.key, vertex);
        }
        edgeList.add(null); // 0 means end
    }

    public void addEdge(VertexKey from, VertexKey to) {
        Edge edge = Edge.builder()
                .next(head.getOrDefault(from, 0))
                .to(to)
                .weight(Vertex.euclideanDistance(vertexMap.get(from), vertexMap.get(to)))
                .build();
        edgeList.add(edge);
        head.put(from, edgeList.size() - 1);
    }

    public Vertex nearestVertex(int floor, double x, double y) {
        if (vertexList.size() == 0) {
            logger.warn("no vertex");
            return new Vertex();
        }
        Vertex center = Vertex.builder()
                .x(x)
                .y(y)
                .build();
        List<Vertex> sameFloorVertex = vertexList.stream()
                .filter(v -> v.key.floor == floor)
                .collect(Collectors.toList());
        Vertex nearest = sameFloorVertex.get(0);
        double shortestDistance = Vertex.euclideanDistance(center, nearest);
        for (Vertex vertex : sameFloorVertex) {
            double d = Vertex.euclideanDistance(center, vertex);
            if (d < shortestDistance) {
                nearest = vertex;
                shortestDistance = d;
            }
        }

        return nearest;
    }

    public List<Edge> getNeighbours(VertexKey v) {
        List<Edge> neighbours = new ArrayList<>();
        for (int i = head.get(v); i != 0; i = edgeList.get(i).next) {
            neighbours.add(edgeList.get(i));
        }
        return neighbours;
    }

    public double getHScore(VertexKey v, Vertex dst) {
        return 0.0; // TODO Dij
    }

    public Vertex getVertexByKey(VertexKey key) {
        return vertexMap.get(key);
    }
    //    private double searchInSameLayout(Vertex v1, Vertex v2) {
    //
    //    }
}
