package org.app.core.data.shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public enum ShaderType {
    SHADER_TYPE_VERTEX(GL_VERTEX_SHADER, "vert"),
    SHADER_TYPE_FRAGMENT(GL_FRAGMENT_SHADER, "frag");

    private int type;
    private String ext;

    ShaderType(int type, String ext) {
        this.type = type;
        this.ext = ext;
    }

    public int getType() {
        return type;
    }

    public String getExt() {
        return ext;
    }
}
