#version 150
in vec2 inPosition;// input from the vertex buffer

out vec2 texCoord;

void main() {
    texCoord = inPosition;

    // grid je <0;1> - potřebujeme <-1;1>, protože takový je rozsah obrazovky
    vec2 position = inPosition * 2 - 1;
    gl_Position = vec4(position, 0, 1.0);
} 
