package org.app.core.components;

import glm_.vec3.Vec3;
import glm_.vec4.Vec4;

public class Camera extends Component {
    private Vec3 translation;
    private Vec4 rotation;
    private float fov;

    public Camera(Vec3 translation, Vec4 rotation, float fov) {
        this.translation = translation;
        this.rotation = rotation;
        this.fov = fov;
    }

    public Vec3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vec3 translation) {
        this.translation = translation;
    }

    public Vec4 getRotation() {
        return rotation;
    }

    public void setRotation(Vec4 rotation) {
        this.rotation = rotation;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }
}
