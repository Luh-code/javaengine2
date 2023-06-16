package org.app.core.data.shader;

import org.apache.commons.io.FileUtils;
import org.app.core.GLManager;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.app.utils.Logger;

public class Shader {
    private ShaderType type;

    private CharSequence shaderSource;

    public Shader(ShaderType type, CharSequence shaderSource) {
        this.type = type;
        this.shaderSource = shaderSource;
    }

    public Shader(ShaderType type, File file) {
        String fileExt = FilenameUtils.getExtension(file.toString());
        if (fileExt.equals("glsl"))
        {
            Logger.logWarn("File extension .glsl is ambiguous for shaders, use specialized file extensions (.vert, .frag)");
        }
        else if (!type.getExt().equals(fileExt))
        {
            Logger.logWarn("Possible mismatch: File extension ." + fileExt
             + " is not designated for use with " + type);
        }

        this.type = type;
        try {
            this.shaderSource = FileUtils.readFileToString(file, "UTF-8");
            Logger.logDebug("Successfully read " + type.toString()
                + " from file: " + file);
        } catch (IOException e) {
            Logger.logAndThrow("An error occurred whilst reading: "
                    + file, RuntimeException.class);
        }
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
