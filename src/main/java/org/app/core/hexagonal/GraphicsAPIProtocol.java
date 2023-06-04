package org.app.core.hexagonal;

public interface GraphicsAPIProtocol {
    void create_shader(String shaderSource, ShaderType shaderType);
}
