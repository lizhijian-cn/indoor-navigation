package org.wangpai.navigation.core.util;

import org.wangpai.navigation.core.vertex.Vertex;

public class GraphHelper {
    // TODO handle exception
    public Vertex parseVertex(String s) {
        String[] ss = s.split(" ");
        int floor = Integer.parseInt(ss[0]);
        if (ss.length == 2) {
            return Vertex.builder()
                    .floor(floor)
                    .
        }
    }
}
