package org.app.core.components;

import glm_.vec3.Vec3;
import glm_.vec4.Vec4;
import org.app.core.data.Material;
import org.app.core.data.Mesh;

public class Actor extends Component {
    private Vec3 translation;
    private Vec4 rotation;
    private Mesh mesh;
    private Material material;

    public Actor(Vec3 translation, Vec4 rotation, Mesh mesh, Material material) {
        this.translation = translation;
        this.rotation = rotation;
        this.mesh = mesh;
        this.material = material;
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

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
