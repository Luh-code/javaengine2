#version 330 core
out vec4 FragColor;

in vec3 vertexColor;
in vec2 TexCoord;
//uniform vec4 myColor;

uniform sampler2D texture1;

void main()
{
    FragColor = mix(texture(texture1, TexCoord), vec4(TexCoord.x, TexCoord.y, 0.0, 1.0), 0.0);
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