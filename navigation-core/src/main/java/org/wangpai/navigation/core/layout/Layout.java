package org.wangpai.navigation.core.layout;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangpai.navigation.core.edge.Edge;
import org.wangpai.navigation.core.vertex.Vertex;

import java.util.List;

/**
 * 一层的图
 * 目前暂用邻接矩阵
 * 日后需抽象接口，搞成邻接表
 */
@Data
public class Layout {
    private static Logger logger = LoggerFactory.getLogger(Layout.class);


    List<Integer> head;
    List<Edge> edges;

    int floor;
    int vertexNum;
    Vertex[] vertexes;
    List<Integer> stairwayVertex;
    List<Integer> elevatorVertex;


    public void init(List<Vertex> vertexList) {
        this.vertexNum = vertexList.size();
        this.vertexNum = vertexNum;

    }


    // TODO optimise
    public Vertex nearestVertex(double x, double y) {
        if (vertexNum == 0) {
            logger.warn("no vertex in layout {}", floor);
            return new Vertex();
        }
        Vertex center = Vertex.builder().x(x).y(y).build();

        Vertex nearest = vertexes[0];
        double shortestDistance = Vertex.euclideanDistance(center, nearest);
        for (Vertex vertex : vertexes) {
            double d = Vertex.euclideanDistance(center, vertex);
            if (d < shortestDistance) {
                nearest = vertex;
                shortestDistance = d;
            }
        }

        return nearest;
    }

}