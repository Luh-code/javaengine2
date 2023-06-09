package org.app.core.data;

import glm_.vec3.Vec3;

public class Vertex {
    private Vec3 translation;

    public Vertex(Vec3 translation) {
        this.translation = translation;
    }

    public float[] getFloats()
    {
        return translation.getArray();
    }

    public Vec3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vec3 translation) {
        this.translation = translation;
    }
}
