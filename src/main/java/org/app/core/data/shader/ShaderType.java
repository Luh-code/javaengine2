package org.app.core.data.shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public enum ShaderType {
    SHADER_TYPE_VERTEX(GL_VERTEX_SHADER),
    SHADER_TYPE_FRAGMENT(GL_FRAGMENT_SHADER);

    private int type;

    ShaderType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
