package org.ah.cpc.util;

import com.badlogic.gdx.math.Vector3;

public class VerticeBuilder {

    Vector3 verticeTemp = new Vector3();

    int i = 0;
    float[] vertices;

    public VerticeBuilder(int vertices) {
        this.vertices = new float[vertices * 12];
    }

    public VerticeBuilder addVertice(float x, float y, float z, float tx, float ty) {
        verticeTemp.x = -x;
        verticeTemp.y = -y;
        verticeTemp.z = -z;
        verticeTemp.nor();
        return addVertice(x, y, z, verticeTemp.x, verticeTemp.y, verticeTemp.z, tx, ty);
    }

    public VerticeBuilder addVertice(float x, float y, float z, float nx, float ny, float nz, float tx, float ty) {
        return addVertice(x, y, z, 1f, 1f, 1f, 1f, nx, ny, nz, tx, ty);
    }

    public VerticeBuilder addVertice(
            float x, float y, float z,
            float r, float g, float b, float a,
            float nx, float ny, float nz,
            float tx, float ty) {
        vertices[i++] = x;
        vertices[i++] = y;
        vertices[i++] = z;
        vertices[i++] = r;
        vertices[i++] = g;
        vertices[i++] = b;
        vertices[i++] = a;
        vertices[i++] = nx;
        vertices[i++] = ny;
        vertices[i++] = nz;
        vertices[i++] = tx;
        vertices[i++] = ty;
        return this;
    }

    public float[] vertices() {
        return vertices;
    }
}
