package org.wangpai.navigation.core.vertex;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wangpai.navigation.core.layout.Layout;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vertex {
    public VertexKey key;
    public VertexTypeEnum type;
    public double x, y;

//    public int getFloor() {
//        return layout.getFloor();
//    }

    public static double manhattanDistance(Vertex v1, Vertex v2) {
        return Math.abs(v1.x - v2.x) + Math.abs(v1.y - v2.y);
    }

    public static double euclideanDistance(Vertex v1, Vertex v2) {
        return Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2));
    }
}
