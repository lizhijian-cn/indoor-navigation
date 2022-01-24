package org.wangpai.navigation.core.vertex;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vertex {
    public VertexKey key;
    public VertexTypeEnum type;
    public int typeIdx;
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

    @Override
    public String toString() {
        return "Vertex{" + "key=" + key + ", type=" + type + ", typeIdx=" + typeIdx + ", x=" + x + ", y=" + y + '}';
    }
}
