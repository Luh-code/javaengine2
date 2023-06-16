package org.app.core.data;

import org.app.core.data.shader.ShaderProgram;

public class Material {
    private ShaderProgram shaderProgram;
    private Texture texture;

    public Material(ShaderProgram shaderProgram, Texture texture) {
        this.shaderProgram = shaderProgram;
        this.texture = texture;
    }

    public void compile()
    {
        shaderProgram.compile(false);
        if ( texture != null )
            texture.generate(true);
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
