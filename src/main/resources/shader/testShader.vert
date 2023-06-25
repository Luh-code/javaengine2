#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aTexCoord;

out vec3 vertexColor;
out vec2 TexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    vertexColor = aColor;
    TexCoord = aTexCoord;
}

//#version 330 core
//layout (location = 0) in vec3 aPos;
//layout (location = 1) in vec3 aCol;
//
//out vec4 vertexColor;
//
//void main()
//{
//    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
//    //vertexColor = vec4(1.0, 0.5, 0.2, 1.0);
//    vertexColor = vec4(aCol, 1.0);
//}