package org.wangpai.navigation.core.algo.astar;

import org.wangpai.navigation.core.edge.Edge;
import org.wangpai.navigation.core.graph.Graph;
import org.wangpai.navigation.core.vertex.Vertex;
import org.wangpai.navigation.core.vertex.VertexKey;

import java.util.*;

public class AStar {

    final static double EPS = 10e-7;
    Graph graph;

    public AStar(Graph graph) {
        this.graph = graph;
    }

    public double search(int floorSrc, double xSrc, double ySrc, int floorDst, double xDst,
                         double yDst) {
        Vertex src = graph.nearestVertex(floorSrc, xSrc, ySrc);
        Vertex dst = graph.nearestVertex(floorDst, xDst, yDst);
        search0(src, dst);
        return 0;
    }

    private double search0(Vertex src, Vertex dst) {

        Set<VertexKey> openSet = new HashSet<>(), closedSet = new HashSet<>();
        Map<VertexKey, Double> g = new HashMap<>(), h = new HashMap<>();
        Map<VertexKey, VertexKey> parents = new HashMap<>();
        PriorityQueue<VertexKey> pq = new PriorityQueue<>(new Comparator<VertexKey>() {
            @Override
            public int compare(VertexKey o1, VertexKey o2) {
                double res = g.get(o1) + h.get(o1) - g.get(o2) - h.get(o2);
                return res < EPS && res > -EPS ? 0 : res > EPS ? 1 : -1;
            }
        });

        g.put(src.key, 0.0);
        h.put(src.key, 0.0);
        parents.put(src.key, src.key);
        pq.add(src.key);

        while (!pq.isEmpty()) {
            VertexKey cur = pq.poll();
            List<Edge> neighbours = graph.getNeighbours(cur);

            for (Edge edge : neighbours) {
                VertexKey neighbour = edge.to;
                if (closedSet.contains(neighbour)) {
                    continue;
                }
                double ng = g.get(cur) + edge.weight;
                if (openSet.contains(neighbour)) {
                    if (g.get(neighbour) < ng) {
                        g.put(neighbour, ng);
                        parents.put(neighbour, cur);
                        pq.remove(neighbour);
                        pq.add(neighbour);
                    }
                } else {
                    openSet.add(neighbour);
                    g.put(neighbour, ng);
                    h.put(neighbour, graph.getHScore(neighbour, dst));
                    pq.add(neighbour);
                    parents.put(neighbour, cur);
                    if (neighbour.equals(dst)) {
                        System.out.printf("find target");
                        pq.clear();
                        break;
                    }
                }
            }

            closedSet.add(cur);
        }
        return 0.0;
    }
}
