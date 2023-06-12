package org.app.core.data.shader;

import org.app.core.RenderSystem;
import org.app.utils.Logger;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.function.BiConsumer;

import static org.app.core.GLManager.compileShader;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ShaderProgram implements AutoCloseable {
    private Shader vertexShader;
    private Shader fragmentShader;

    private int shaderProgram;

    private BiConsumer<RenderSystem, ShaderProgram> uniformUpdater;

    public ShaderProgram(Shader vertexShader, Shader fragmentShader,
                         BiConsumer<RenderSystem, ShaderProgram> uniformUpdater) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.uniformUpdater = uniformUpdater;
    }

    public void compile(boolean keepAfterCompile)
    {
        int vertexShaderHandle = this.vertexShader.compile();
        int fragmentShaderHandle = this.fragmentShader.compile();

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShaderHandle);
        glAttachShader(shaderProgram, fragmentShaderHandle);
        glLinkProgram(shaderProgram);

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer success = stack.mallocInt(1);
            glGetProgramiv(shaderProgram, GL_LINK_STATUS, success);

            if ( success.get(0) == GL_FALSE )
            {
                String log = glGetProgramInfoLog(shaderProgram);
                Logger.logError("An error occurred whilst linking a shaderProgram: " + log);
            }
        }
        glDeleteShader(vertexShaderHandle);
        glDeleteShader(fragmentShaderHandle);
        if (keepAfterCompile) return;
        this.vertexShader = null;
        this.fragmentShader = null;
    }

    public void updateUniforms(RenderSystem renderSystem)
    {
        if ( uniformUpdater == null ) return;
        uniformUpdater.accept(renderSystem, this);
    }

    public int getShaderProgram() {
        return shaderProgram;
    }

    public Shader getVertexShader() {
        return vertexShader;
    }

    public Shader getFragmentShader() {
        return fragmentShader;
    }

    @Override
    public void close() throws Exception {
        glDeleteProgram(shaderProgram);
    }
}
