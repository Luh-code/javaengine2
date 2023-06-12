package org.app.core.data;

import glm_.vec3.Vec3;
import glm_.vec4.Vec4;
import org.apache.commons.lang3.ArrayUtils;

public class Vertex {
    private Vec3 translation;
    private Vec3 color;

    public Vertex(Vec3 translation, Vec3 color) {
        this.translation = translation;
        this.color = color;
    }

    public float[] getFloats()
    {
        return ArrayUtils.addAll(translation.getArray(), color.getArray());
    }

    public Vec3 getColor() {
        return color;
    }

    public void setColor(Vec3 color) {
        this.color = color;
    }

    public Vec3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vec3 translation) {
        this.translation = translation;
    }
}
