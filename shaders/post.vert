#version 150
in vec2 inPosition;// input from the vertex buffer

out vec2 texCoord;//souradnice do textury

void main() {
    texCoord = inPosition;//napr grid <0;1>

    // grid je <0;1> - potřebujeme <-1;1>, protože takový je rozsah obrazovky
    vec2 position = inPosition*4-1;//* 2; //- 1;
    position.x-=1;
    position.y-=2;

    gl_Position = vec4(position, 0, 1.0);

}
//mapovani pomoci matic bod po bodu