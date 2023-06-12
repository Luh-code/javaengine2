package org.app.core.data;

import org.app.core.data.shader.ShaderProgram;

public class Material {
    private ShaderProgram shaderProgram;

    public Material(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public void compile()
    {
        shaderProgram.compile(false);
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}
