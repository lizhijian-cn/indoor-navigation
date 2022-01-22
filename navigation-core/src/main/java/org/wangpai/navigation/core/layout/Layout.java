package org.wangpai.navigation.core.layout;

import lombok.Data;
import org.wangpai.navigation.core.vertex.Vertex;

import java.util.List;

/**
 * 一层的图
 * 目前暂用邻接矩阵
 * 日后需抽象接口，搞成邻接表
 */
@Data
public class Layout {
    int floor;
    int vertexNum;
    List<Integer> stairwayVertex;
    List<Integer> elevatorVertex;

    double[] gScore, hScore, fScore;
    public void init(int vertexNum) {
        this.vertexNum = vertexNum;

    }

    public double aStar(int vertexIdx1, int vertexIdx2) {

    }

    private double hFunc(Vertex v1, Vertex v2) {
        return Math.abs(v1.getX() - v2.getX()) + Math.abs(v1.getY() - v2.getY());
    }
}
