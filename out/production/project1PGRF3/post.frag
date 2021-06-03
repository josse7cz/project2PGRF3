#version 150
in vec2 texCoord;//souradnice do textury
uniform sampler2D textureRendered;
uniform sampler2D textureDepth;
uniform float width;
uniform float height;
out vec4 outColor;// output from the fragment shader
vec4 finalColor;
float resolution=1920;//1080;//Math.sqrt(width*height);
const float radius=8;
vec2 dir;


void main() {
    dir = vec2(1.f, 1.f);//mozna chyba zde

    vec4 sum = vec4(0.0);
    vec2 tc = texCoord;
    float blur = radius/resolution;
    float hstep = dir.x;
    float vstep = dir.y;
    vec4 textureColor = texture(textureRendered, texCoord);
    vec4 depth = texture (textureDepth, texCoord);


if (depth.r>0.991f){

        sum += texture2D(textureRendered, vec2(tc.x - 4.0*blur*hstep, tc.y - 4.0*blur*vstep)) * 0.0162162162;
        sum += texture2D(textureRendered, vec2(tc.x - 3.0*blur*hstep, tc.y - 3.0*blur*vstep)) * 0.0540540541;
        sum += texture2D(textureRendered, vec2(tc.x - 2.0*blur*hstep, tc.y - 2.0*blur*vstep)) * 0.1216216216;
        sum += texture2D(textureRendered, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.1945945946;
        sum += texture2D(textureRendered, vec2(tc.x, tc.y)) * 0.2270270270;
        sum += texture2D(textureRendered, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.1945945946;
        sum += texture2D(textureRendered, vec2(tc.x + 2.0*blur*hstep, tc.y + 2.0*blur*vstep)) * 0.1216216216;
        sum += texture2D(textureRendered, vec2(tc.x + 3.0*blur*hstep, tc.y + 3.0*blur*vstep)) * 0.0540540541;
        sum += texture2D(textureRendered, vec2(tc.x + 4.0*blur*hstep, tc.y + 4.0*blur*vstep)) * 0.0162162162;
        outColor=textureColor*vec4(sum.rgb, 1.0);

    }
    else {

        outColor=textureColor*vec4(0.6,0.6,0.8,1.0);
    }



    	if (gl_FragCoord.y < 200) {//promenna pro cast obrazovky
    		float grey = textureColor.r * 0.33 + textureColor.g * 0.33 + textureColor.b * 0.33;
    		outColor = vec4(grey, grey, grey, 1);
    	}
    //outColor=vec4(depth.r,0,0,1);

}
