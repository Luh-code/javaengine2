#version 330 core
out vec4 FragColor;

in vec3 vertexColor;
in vec2 TexCoord;
//uniform vec4 myColor;

uniform sampler2D texture1;
uniform sampler2D texture2;

void main()
{
    FragColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.5);
}

//#version 330 core
//out vec4 FragColor;
//
//in vec4 vertexColor;
//uniform vec4 myColor;
//
//void main()
//{
//    //FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
//    FragColor = vertexColor;
//    //FragColor = myColor;
//}