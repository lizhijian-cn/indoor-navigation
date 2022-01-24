package org.wangpai.navigation.core.vertex;

import lombok.Builder;

/**
 * identify a vertex
 */
@Builder
public class VertexKey {
    public int floor;
    public int idx;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VertexKey vertexKey = (VertexKey) o;

        if (floor != vertexKey.floor) return false;
        return idx == vertexKey.idx;
    }

    @Override
    public int hashCode() {
        int result = floor;
        result = 31 * result + idx;
        return result;
    }
}
