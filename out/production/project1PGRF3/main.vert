#version 150
in vec2 inPosition;// input from the vertex buffer

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;
uniform float type;
uniform float time;
uniform vec4 position;


out vec2 texCoord;

const float PI = 3.1415;

float getZ(vec2 vec) {
    return 0.5 * cos(sqrt(25 * vec.x * vec.x + 20 * vec.y * vec.y*time));
}

vec3 getSphere(vec2 vec) {
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI *0.5;// <-1;1> -> <-PI/2;PI/2>
    float r = 1.0;

    float x = r * cos(az) * cos(ze);
    float y = 2 * r * sin(az) * cos(ze);
    float z = 0.5 * r * sin(ze);
    return vec3(x, y, z);
}
vec3 getBall(vec2 vec) {
    float s =PI * 0.5 - PI * vec.x;
    float t = 2* PI * vec.y;
    float r = 2;

    return vec3(
    cos(t) * cos(s) * r*0.5,
    sin(t) * cos(s) * r*0.5,
    sin(s) * r*0.5);
}
vec3 getElephant(vec2 vec) {
    float az=(vec.x+1)*PI;
    float ze=(vec.y+1)*2*PI;
    float r= 3+cos(4*ze);
    float x = r*sin(ze)*cos(az);
    float y = r*sin(ze)*sin(az);
    float z = r*cos(ze);
    return vec3(x*0.5, y*0.5, z*0.5);
}
vec3 getSombrero(vec2 vec) {
    float t=2*PI*vec.y;
    float s=PI*0.5-PI*vec.x*2;

    return vec3(t*cos(s)*0.5, t*(sin(s))*0.5, 2*sin(t)/2*0.5);
}
vec3 getCube(vec2 vec) {
    float s = vec.x * 2;
    float t = vec.y * 2 * PI;
    return vec3((1-s)*(3+cos(t))*cos(2*PI*s)*0.5,(1-s)*(3+cos(t))*sin(2*PI*s)*0.5,6*s+(1-s)*sin(t))/2*0.5;



}

void main() {
    texCoord = inPosition;
    // grid je <0;1> - chci <-1;1>
    vec2 position = inPosition * 2 - 1;
    //    vec3 normal = normalize(normal(inPosition.x, inPosition.y));//normála
    //    normal = inverse(transpose(mat3(matMV)))*normal;
    //    vec3 lightDirection = normalize(lightSource - posMV.xyz);
    //    intensity = dot(lightDirection, normal);
    //    vertColor = vec3(normal.xyz);


    vec3 finalPosition;
    if (type == 0) {
        finalPosition = getSphere(position);
    }
    //         if (type==15){
    //            finalPosition = getJuicer();
    //        }
    if (type==1) {
        finalPosition = vec3(position, getZ(position));
    }if (type==2) {
        finalPosition =  getCube(position);
    }
    if (type==3) {
        finalPosition =  getElephant(position);
    }
    if (type==4) {
        finalPosition =  getSombrero(position);
    }
    if (type==5) {
        finalPosition = getBall((position));
    }


    vec4 pos4 = vec4(finalPosition, 1.0);
    gl_Position = projection * view *model*pos4;


} 
