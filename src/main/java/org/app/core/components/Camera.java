package org.app.core.components;

import glm_.glm;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;

public class Camera extends Component {
    private Vec3 translation;
    private Vec3 target;
    private Vec3 direction;
    private Vec3 front;
    private Vec4 rotation;
    private Vec3 upDirection;
    private Vec3 right;
    private Vec3 up;
    private float fov;

    public Camera(Vec3 translation, Vec4 rotation, Vec3 target, float fov, Vec3 upDirection, Vec3 front) {
        this.translation = translation;
        this.rotation = rotation;
        this.target = target;
        this.fov = fov;
        this.upDirection = upDirection;
        this.front = front;
    }

    public void updateCamera() {
        direction = translation.minus(target).normalize();
        right = upDirection.cross(direction).normalize();
        up = direction.cross(right);
    }

    public void lookAt(Vec3 t) {
        front = t.minus(translation).normalize();
    }

    public Mat4 getLookAt() {
        glm glmi = glm.INSTANCE;
        return glmi.lookAt(translation, translation.plus(front), upDirection);
    }

    public Vec3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vec3 translation) {
        this.translation = translation;
    }

    public Vec3 getTarget() {
        return target;
    }

    public void setTarget(Vec3 target) {
        this.target = target;
    }

    public Vec3 getDirection() {
        return direction;
    }

    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }

    public Vec4 getRotation() {
        return rotation;
    }

    public void setRotation(Vec4 rotation) {
        this.rotation = rotation;
    }

    public Vec3 getUpDirection() {
        return upDirection;
    }

    public void setUpDirection(Vec3 upDirection) {
        this.upDirection = upDirection;
    }

    public Vec3 getRight() {
        return right;
    }

    public void setRight(Vec3 right) {
        this.right = right;
    }

    public Vec3 getUp() {
        return up;
    }

    public void setUp(Vec3 up) {
        this.up = up;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public Vec3 getFront() {
        return front;
    }

    public void setFront(Vec3 front) {
        this.front = front;
    }
}
