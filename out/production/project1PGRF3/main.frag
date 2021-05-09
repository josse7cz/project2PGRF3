#version 150
in vec2 texCoord;
in float typeTexture;
in vec3 normal,FragPos,Normal;
uniform float type;
uniform sampler2D textureBall,textureWood,textureMosaic,textureEarth;

uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 objectColor;
uniform vec3 lightColor;

out vec4 position;
vec4 textureColor;
out vec4 outColor;// output from the fragment shader
vec2 coord;

void main() {
//ambient
    float ambientStrength = 10.1f;
    vec3 ambient= ambientStrength*lightColor;
    //diffuse
    vec3 normal=normalize(Normal);
    vec3 lightDir=normalize(lightPos-FragPos);
    float diff=max(dot(normal,lightDir),0.0);
    vec3 diffuse=diff*lightColor;
    //specular
    float specularStrength=0.5f;
    vec3 viewDir=normalize(viewPos-FragPos);
    vec3 reflectDir = reflect (-lightDir,normal);
    float spec= pow(max(dot(viewDir,reflectDir),0.0),32);
    vec3 specular= specularStrength*spec*lightColor;
    vec3 result=(ambient+diffuse+specular)*objectColor;





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

    outColor=texture(textureEarth, texCoord)*vec4(result,0.8f);

    outColor=vec4(result,0.8f);
   // outColor = textureColor;
} 
