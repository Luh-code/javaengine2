package org.app.core.data.shader;

import org.app.core.GLManager;

public class Shader {
    private ShaderType type;

    private CharSequence shaderSource;

    public Shader(ShaderType type, CharSequence shaderSource) {
        this.type = type;
        this.shaderSource = shaderSource;
    }

    public int compile()
    {
        return GLManager.compileShader(type.getType(), shaderSource);
    }

    public ShaderType getType() {
        return type;
    }

    public CharSequence getShaderSource() {
        return shaderSource;
    }
}
