package org.app.core.data.shader;

import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec2.Vec2ui;
import glm_.vec3.Vec3;
import glm_.vec3.Vec3i;
import glm_.vec3.Vec3ui;
import glm_.vec4.Vec4;
import glm_.vec4.Vec4i;
import glm_.vec4.Vec4ui;
import org.app.core.RenderSystem;
import org.app.utils.Logger;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.function.BiConsumer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL30.*;
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

    public void use()
    {
        glUseProgram(this.shaderProgram);
    }

    public int getUniformLocation(String name)
    {
        return glGetUniformLocation(shaderProgram, name);
    }

    public void setFloat(String name, float value)
    {
        int position = getUniformLocation(name);
        glUniform1f(position, value);
    }
    public void setFloat2(String name, Vec2 value)
    {
        int position = getUniformLocation(name);
        glUniform2f(position, value.getX(), value.getY());
    }
    public void setFloat3(String name, Vec3 value)
    {
        int position = getUniformLocation(name);
        glUniform3f(position, value.getX(), value.getY(), value.getZ());
    }
    public void setFloat4(String name, Vec4 value)
    {
        int position = getUniformLocation(name);
        glUniform4f(position, value.getX(), value.getY(), value.getZ(), value.getW());
    }

    public void setInt(String name, int value)
    {
        int position = getUniformLocation(name);
        glUniform1i(position, value);
    }
    public void setInt2(String name, Vec2i value)
    {
        int position = getUniformLocation(name);
        glUniform2i(position, value.getX(), value.getY());
    }
    public void setInt3(String name, Vec3i value)
    {
        int position = getUniformLocation(name);
        glUniform3i(position, value.getX(), value.getY(), value.getZ());
    }
    public void setInt4(String name, Vec4i value)
    {
        int position = getUniformLocation(name);
        glUniform4i(position, value.getX(), value.getY(), value.getZ(), value.getW());
    }

    public void setUInt(String name, int value)
    {
        int position = getUniformLocation(name);
        glUniform1i(position, value);
    }
    public void setUInt2(String name, Vec2ui value)
    {
        int position = getUniformLocation(name);
        glUniform2ui(position, value.getX().toInt(), value.getY().toInt());
    }
    public void setUInt3(String name, Vec3ui value)
    {
        int position = getUniformLocation(name);
        glUniform3ui(position, value.getX().toInt(), value.getY().toInt(), value.getZ().toInt());
    }
    public void setInt4(String name, Vec4ui value)
    {
        int position = getUniformLocation(name);
        glUniform4ui(position, value.getX().toInt(), value.getY().toInt(), value.getZ().toInt(), value.getW().toInt());
    }

    /*
     TODO: Implement wrappers for glUniform1fv, glUniform2fv, glUniform3fv, glUniform4fv,
        glUniform1iv, glUniform2iv, glUniform3iv, glUniform4iv, glUniform1uiv, glUniform2uiv,
        glUniform3uiv, glUniform4uiv, glUniformMatrix2fv, glUniformMatrix3fv, glUniformMatrix4fv,
        glUniformMatrix2x3fv, glUniformMatrix3x2fv, glUniformMatrix2x4fv, glUniformMatrix4x2fv,
        glUniformMatrix3x4fv, glUniformMatrix4x3fv
     */

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
