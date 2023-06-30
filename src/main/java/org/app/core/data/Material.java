package org.app.core.data;

import org.app.core.data.shader.ShaderProgram;
import org.app.utils.Logger;

public class Material {
    private ShaderProgram shaderProgram;
    private Texture[] textures;

    public Material(ShaderProgram shaderProgram, Texture[] textures) {
        this.shaderProgram = shaderProgram;
        this.textures = textures;
    }

    public void compile()
    {
        shaderProgram.compile(true);
        if ( textures != null ) {
            for (Texture texture : textures) {
                if (texture != null)
                    texture.generate(true);
            }
        }
    }

    public void apply()
    {
        for (int i = 0; i < textures.length; i++) {
            if ( textures[i] != null )
                textures[i].apply();
            else
                Logger.logError("Texture " + i + " cannot be null in " + this);
        }
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public Texture[] getTextures() {
        return textures;
    }
    public Texture getTexture(int i) {
        if ( i < textures.length && i > 0 )
            return textures[i];
        Logger.logError("Cannot get texture " + i + ", as it isn't valid. Returning null");
        return null;
    }

    public void setTextures(Texture[] textures) {
        this.textures = textures;
    }
}
