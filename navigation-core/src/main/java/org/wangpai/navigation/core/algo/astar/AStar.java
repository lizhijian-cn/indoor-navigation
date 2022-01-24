package org.wangpai.navigation.core.algo.astar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangpai.navigation.core.edge.Edge;
import org.wangpai.navigation.core.graph.Graph;
import org.wangpai.navigation.core.vertex.Vertex;
import org.wangpai.navigation.core.vertex.VertexKey;

import java.util.*;

public class AStar {
    private static final Logger logger = LoggerFactory.getLogger(AStar.class);

    final static double EPS = 10e-7;
    Graph graph;

    public AStar(Graph graph) {
        this.graph = graph;
    }

    public void search(int floorSrc, double xSrc, double ySrc, int floorDst, double xDst,
                         double yDst) {
        Vertex src = graph.nearestVertex(floorSrc, xSrc, ySrc);
        Vertex dst = graph.nearestVertex(floorDst, xDst, yDst);
        System.out.println("src = " + src);
        System.out.println("dst = " + dst);

        search0(src, dst);
    }

    private void search0(Vertex src, Vertex dst) {

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
//            System.out.println(cur);
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
                    if (neighbour.equals(dst.key)) {
                        System.out.printf("find target");
                        printPath(dst.key, parents);
                        pq.clear();
                        return;
                    }
                }
            }

            closedSet.add(cur);
        }
        System.out.println("could not find path");
        return;
    }

    private void printPath(VertexKey v, Map<VertexKey, VertexKey> parents) {
        while (true) {
            VertexKey p = parents.get(v);
            if (p == null) {
                logger.error("wrong");
                return;
            }
            if (p.equals(v)) {
                break;
            }
            System.out.println(graph.getVertexByKey(p));
            v = p;
        }
    }
}
