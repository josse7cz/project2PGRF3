#version 150
in vec2 texCoord;
in float typeTexture;
in vec3 normal,FragPos,Normal;
uniform float type;
uniform sampler2D textureMosaic,textureEarth;
uniform vec3 lightPos,viewPos,objectCol,lightColor;
out vec4 outColor, position;// output from the fragment shader
vec2 coord;
vec4 textureColor;

void main() {
    //ambient
    float ambientStrength = 8.1f;
    vec3 ambient= ambientStrength*lightColor;
    //diffuse
    vec3 lightDir=normalize(lightPos-FragPos);
    float diff=max(dot(normal,lightDir),0.0);
    vec3 diffuse=diff*lightColor;
    //specular
    float specularStrength=8.5f;
    vec3 viewDir=normalize(viewPos-FragPos);
    vec3 reflectDir = reflect (-lightDir,normal);
    float spec= pow(max(dot(viewDir,reflectDir),0.0),8);
    vec3 specular= specularStrength*spec*lightColor;
    vec3 result=(ambient+diffuse+specular)*objectCol;
    vec2 coord=mod(texCoord*vec2(2.0, 4.0), vec2(1.0, 1.0));


    if (typeTexture==2){

        outColor=texture(textureEarth, texCoord)*vec4(result,1.0f);
    }


outColor=texture(textureMosaic, texCoord)*vec4(result,1.0f);

} 
