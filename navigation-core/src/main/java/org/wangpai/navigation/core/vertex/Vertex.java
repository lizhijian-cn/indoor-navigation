package org.wangpai.navigation.core.vertex;


import lombok.Builder;
import lombok.Data;
import org.wangpai.navigation.core.layout.Layout;

@Data
@Builder
public class Vertex {
    int index;
    VertexTypeEnum type;
    int floor;
    double x;
    double y;

    public int getFloor() {
        return layout.getFloor();
    }
}
