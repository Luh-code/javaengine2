package org.app.core.data.loading;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import glm_.vec2.Vec2;
import glm_.vec3.Vec3;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.app.core.data.Mesh;
import org.app.core.data.Vertex;
import org.app.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class ModelLoader {
    @Deprecated
    private static String readFile(File path) {
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

    @Deprecated
    private static Integer[] parseIndicesOBJ(String face) {
        String[] tokens = face.split("/");

        if ( tokens.length > 3 ) {
            Logger.logAndThrow(String.format(
                    "Tried to parse empty or incomplete OBJ face(count=%d)",
                    tokens.length
            ), RuntimeException.class);
        }

        return new Integer[] {
                Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]),
                Integer.parseInt(tokens[2])
        };
    }

    @Deprecated
    private static Mesh loadOBJ(File path) {
        String[] lines = readFile(path).split("\n");

        ArrayList<Vec3> vertices = new ArrayList<>();
        ArrayList<Vec2> uvs = new ArrayList<>();
        ArrayList<Vec3> normals = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Integer> uvIndices = new ArrayList<>();
        ArrayList<Integer> normalIndices = new ArrayList<>();

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
                    if ( tokens.length > 1 ) indices.addAll(Arrays.asList(parseIndicesOBJ(tokens[1])));
                    if ( tokens.length > 2 ) uvIndices.addAll(Arrays.asList(parseIndicesOBJ(tokens[2])));
                    if ( tokens.length > 3 ) normalIndices.addAll(Arrays.asList(parseIndicesOBJ(tokens[3])));
                    if ( tokens.length > 4 ) {
                        Logger.logWarn(String.format(
                                "Tried to parse more than 3 index groups(%d), discarding others",
                                tokens.length
                        ));
                    }
                }
                default -> {

                }
            }
        }

        Vec3[] uvs1 = new Vec3[uvIndices.size()];
        for (int i = 0; i < uvIndices.size(); ++i) {

        }

        Vertex[] verts = new Vertex[vertices.size()];
        for (int i = 0; i < verts.length; ++i) {
            // TODO: add vertex normal to vertex
            verts[i] = new Vertex(vertices.get(i), new Vec3(), uvs.get(i));
        }

        Integer[] idcs = indices.toArray(new Integer[0]);
        return new Mesh(verts, ArrayUtils.toPrimitive(idcs));
    }
    public static Mesh loadModel(File path, ModelFormat format) {
        Mesh m;
        switch (format) {
            case OBJ -> {
                Obj obj;
                try {
                    obj = ObjUtils.convertToRenderable(ObjReader.read(new FileInputStream(path)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                IntBuffer indices = ObjData.getFaceVertexIndices(obj);
                FloatBuffer vertices = ObjData.getVertices(obj);
                FloatBuffer uvCoords = ObjData.getTexCoords(obj, 2);
                FloatBuffer normals = ObjData.getVertices(obj);

                Vertex[] vertices1 = new Vertex[vertices.limit()/3];
                for (int i = 0; i < vertices1.length; ++i) {
                    int vec3idx = i*3;
                    int vec2idx = i*2;
                    vertices1[i] = new Vertex(
                            new Vec3(vertices.get(vec3idx), vertices.get(vec3idx+1), vertices.get(vec3idx+2)),
                            new Vec3(),
                            new Vec2(uvCoords.get(vec2idx), uvCoords.get(vec2idx+1))
                    );
                }

                int[] indices1 = new int[indices.remaining()];
                indices.get(indices1);
                m = new Mesh(vertices1, indices1);
                //return loadOBJ(path);
            }
            default -> {
                Logger.logError(String.format(
                        "Tried to load unregistered format: '%s' from '%s', returning empty mesh",
                        format, path
                ));
                return new Mesh(new float[0], new int[0]);
            }
        }
        return m;
    }
}
