#version 330 core
                    layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aCol;

out vec4 vertexColor;

void main()
{
    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
    //vertexColor = vec4(1.0, 0.5, 0.2, 1.0);
    vertexColor = vec4(aCol, 1.0);
}