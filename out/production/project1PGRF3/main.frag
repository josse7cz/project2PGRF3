#version 150
in vec2 texCoord;
in float typeTexture;
in vec3 normal;
uniform float type;
uniform sampler2D textureBall,textureWood,textureMosaic,textureEarth;

out vec4 position;
vec4 textureColor;
out vec4 outColor;// output from the fragment shader
vec2 coord;

void main() {
//ambient
    float ambientStrength = 0.1f;
    //vec3 ambient= ambientStrength*lightColor;




    vec3 nNormal=normalize(normal);
    float f = dot(normalize(vec3(0.0, 1.0, 1.0)), nNormal);
    f=max(f, 0.0);
    outColor.rgb=vec3(f);
    outColor.a=1.0;

    vec2 coord=mod(texCoord*vec2(2.0, 4.0), vec2(1.0, 1.0));


    if (typeTexture==0){
        textureColor = texture(textureMosaic, coord);//texCoord);
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
