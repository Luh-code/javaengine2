package org.app.core.data.loading;

import glm_.vec2.Vec2;
import glm_.vec3.Vec3;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.app.core.data.Mesh;
import org.app.core.data.Vertex;
import org.app.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ModelLoader {
    private String readFile(File path) {
        if (!path.isFile()) {
            Logger.logError(String.format(
                    "Tried to load file, that doesn't exist: '%s'",
                    path.getAbsolutePath()
            ));
            return "";
        }
        if (!path.canRead()) {
            Logger.logError(String.format(
                    "Tried to read file '%s' without permission",
                    path.getAbsolutePath()
            ));
            return "";
        }
        try {
            return FileUtils.readFileToString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Mesh loadOBJ(File path) {
        String[] lines = readFile(path).split("\n");

        ArrayList<Vec3> vertices = new ArrayList<>();
        ArrayList<Vec2> uvs = new ArrayList<>();
        ArrayList<Vec3> normals = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v" -> {
                    // Vertex position
                    vertices.add(new Vec3(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                }
                case "vt" -> {
                    // UV coordinate
                    uvs.add(new Vec2(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    ));
                }
                case "vn" -> {
                    // Vertex normal
                    normals.add(new Vec3(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                }
                case "f" -> {
                    // Indices
                    indices.add(Integer.parseInt(tokens[1]));
                    indices.add(Integer.parseInt(tokens[2]));
                    indices.add(Integer.parseInt(tokens[3]));
                }
                default -> {

                }
            }
        }

        Vertex[] verts = new Vertex[vertices.size()];
        for (int i = 0; i < verts.length; ++i) {
            // TODO: add vertex normal to vertex
            verts[i] = new Vertex(vertices.get(i), new Vec3(), uvs.get(i));
        }

        Integer[] idcs = indices.toArray(new Integer[0]);
        return new Mesh(verts, ArrayUtils.toPrimitive(idcs));
    }
    public Mesh loadModel(File path, ModelFormat format) {
        switch (format) {
            case OBJ -> {

            }
        }
    }
}
