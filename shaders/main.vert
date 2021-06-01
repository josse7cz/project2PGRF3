#version 150
in vec2 inPosition;// input from the vertex buffer

const float PI = 3.1415;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;
uniform float type,time,scale;
uniform vec4 position;
out float typeTexture;
out vec2 texCoord;
out vec3 normal, FragPos,Normal;



vec3 normalCalculation(in vec2 inPosition){
    float distance = sqrt(inPosition.x*inPosition.x+inPosition.y*inPosition.y);
    normal.x=scale*PI*sin(distance)/distance*inPosition.x;
    normal.y=scale*PI*sin(distance)/distance*inPosition.y;
    normal.z=1.0;
    return normal;
}

/*
tělesa
*/

float getJuicer(vec2 vec) { //juicer
    return 0.5 * cos(sqrt(25 * vec.x * vec.x + 20 * vec.y * vec.y*time));
}

vec3 getSphere(vec2 vec) { //šiška
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI *0.5;// <-1;1> -> <-PI/2;PI/2>
    float r = 1.0;
    float x = r * cos(az) * cos(ze);
    float y = 2 * r * sin(az) * cos(ze);
    float z = 0.5 * r * sin(ze);
    return vec3(x, y, z);
}
vec3 getEarth(vec2 vec) { //Earth
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI *0.5;// <-1;1> -> <-PI/2;PI/2>
    float r = 1.0;

    float x = r * cos(az) * cos(ze);
    float y = 2* r * sin(az) * cos(ze);
    float z = 0.5 * r * sin(ze)*2.5;
    return vec3(x, y, z);

}

vec3 getGrid(vec2 vec) {
    float x = vec.x;
    float y = vec.y;
    return vec3(x, y, 1);
}

void main() {

    vec2 position = inPosition * 2 - 1;// grid je <0;1> - chci <-1;1>
    texCoord = inPosition;
    normal=normalCalculation(position.xy);
    FragPos=vec3(model*vec4(vec2 (inPosition),1.0f,1.0f));
    Normal=mat3(transpose(inverse(view*model)))*normal;

    vec3 finalPosition;
    if (type == 0) {
        finalPosition = getSphere(position);
        typeTexture=8;}
    if (type==1) {
        finalPosition = vec3(position, getJuicer(position));
        typeTexture=0;}
    if (type==2) {
        finalPosition =  getEarth(position);
        typeTexture=2; }
    if (type==3) {
        finalPosition =  getGrid(position);
        typeTexture=2; }
//    if (type==4) {
//        finalPosition =  getElephant(position);
//        typeTexture=0; }
//    if (type==5) {
//        finalPosition =  getSombrero(position);
//        typeTexture=0; }

    if (type==8) {
        finalPosition = getSphere(position);
        typeTexture=8; }

    gl_Position = projection * view *model*vec4(finalPosition, 1.0);

} 
