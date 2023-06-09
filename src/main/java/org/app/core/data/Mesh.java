package org.app.core.data;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL42.*;

public class Mesh {
    private Vertex[] vertices;
    private int[] indices;

    private int VAO;
    private int VBO;
    private int EBO;

    public Mesh(Vertex[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    public void genBuffers() {
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, this.getFloats(), GL_STATIC_DRAW);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.getIndices(), GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glEnableVertexAttribArray(0);
    }

    public float[] getFloats() {
        float[] floats = new float[vertices.length*3];
        for (int i = 0; i < vertices.length; i++) {
            int offset = i*3;
            float[] vertex = vertices[i].getFloats();
            for (int j = 0; j < 3; j++) {
                floats[j+offset] = vertex[j];
            }
        }
        return floats;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public void setVertices(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public int getVAO() {
        return VAO;
    }

    public void setVAO(int VAO) {
        this.VAO = VAO;
    }

    public int getVBO() {
        return VBO;
    }

    public void setVBO(int VBO) {
        this.VBO = VBO;
    }

    public int getEBO() {
        return EBO;
    }

    public void setEBO(int EBO) {
        this.EBO = EBO;
    }
}
