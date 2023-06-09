package org.app.core.data;

import org.app.utils.Logger;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.app.core.GLManager.compileShader;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Material {
    private CharSequence vertexShaderSource;
    private CharSequence fragmentShaderSource;
    private int shaderProgram;

    public Material(CharSequence vertexShaderSource, CharSequence fragmentShaderSource) {
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;
    }

    public void compile()
    {
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
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
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public CharSequence getVertexShaderSource() {
        return vertexShaderSource;
    }

    public void setVertexShaderSource(CharSequence vertexShaderSource) {
        this.vertexShaderSource = vertexShaderSource;
    }

    public CharSequence getFragmentShaderSource() {
        return fragmentShaderSource;
    }

    public void setFragmentShaderSource(CharSequence fragmentShaderSource) {
        this.fragmentShaderSource = fragmentShaderSource;
    }

    public int getShaderProgram() {
        return shaderProgram;
    }

    public void setShaderProgram(int shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
}
