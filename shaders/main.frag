#version 150
in vec2 texCoord;
in float typeTexture;
uniform float type;
uniform sampler2D textureMosaic;
uniform sampler2D textureBall;
uniform sampler2D textureWood;
uniform sampler2D textureEarth;
out vec4 position;
vec4 textureColor;
out vec4 outColor;// output from the fragment shader

void main() {

    //

    if (typeTexture==0){
        textureColor = texture(textureMosaic, texCoord);
    }if (typeTexture==1){
        textureColor = texture(textureWood, texCoord);
    }
    if (typeTexture==2){
        textureColor = texture(textureEarth, texCoord);
    }
//    if (typeTexture==3){
//        textureColor = texture(textureWood, texCoord);
//    }
//    if (typeTexture==4){
//        textureColor = texture(textureWood, texCoord);
//    }
//
//    if (typeTexture==5){
//        textureColor = outColor= vec4(0.5,0,0.5,0.8);//texture(textureBall, texCoord);
//    }
//    if(typeTexture==6){
//        textureColor = vec4(1.0, 0.0, 1.0, 0.8);
//    }


    //outColor = vec4(1.0, 0.0, 1.0, 0.8);
    //gl_FragColor = vec4(vec3(position.x,position.y,position.z), 1.0);
    outColor = textureColor;


} 
