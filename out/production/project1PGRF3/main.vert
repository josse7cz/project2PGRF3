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
    float az = vec.x * PI; // <-1;1> -> <-PI;PI>
    float ze = vec.y * PI / 2.0; // <-1;1> -> <-PI/2;PI/2>
    float r = 1.0;

    float x = r * cos(az) * cos(ze);
    float y = 2 * r * sin(az) * cos(ze);
    float z = 0.5 * r * sin(ze);
    return vec3(x, y, z);
}
//vec3 getJuicer(vec2 vec) {
//    float a= -2;
//    float b= 0.73;
//    float az = abs(vec.x*6); // <-1;1> -> <-PI;PI>
//    float ze = abs(vec.y*6); // <-1;1> -> <-PI/2;PI/2>
//    float x = cos(az)*(a + b*cos(ze));
//    float y = sin(az)*(a + b*cos(ze));
//    float z = b*sin(ze);
//
//
//    return vec3(x, y, z);
//}

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
    if(type==1) {
        finalPosition = vec3(position, getZ(position));
    }
    vec4 pos4 = vec4(finalPosition, 1.0);
    gl_Position = projection * view * model*pos4;


} 
