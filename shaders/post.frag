#version 150
in vec2 texCoord;//souradnice do textury
uniform sampler2D textureRendered;
uniform sampler2D textureDepth;
uniform float width;
uniform float height;
out vec4 outColor; // output from the fragment shader
vec4 finalColor;
const float resolution=1980;
const float radius=10;
vec2 dir;


void main() {
	dir = vec2(texCoord.x, texCoord.y);//mozna chyba zde

	vec4 sum = vec4(0.0);
	vec2 tc = texCoord;
	float blur = radius/resolution;
	float hstep = dir.x;
	float vstep = dir.y;
	sum += texture2D(textureRendered, vec2(tc.x - 4.0*blur*hstep, tc.y - 4.0*blur*vstep)) * 0.0162162162;
	sum += texture2D(textureRendered, vec2(tc.x - 3.0*blur*hstep, tc.y - 3.0*blur*vstep)) * 0.0540540541;
	sum += texture2D(textureRendered, vec2(tc.x - 2.0*blur*hstep, tc.y - 2.0*blur*vstep)) * 0.1216216216;
	sum += texture2D(textureRendered, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.1945945946;
	sum += texture2D(textureRendered, vec2(tc.x, tc.y)) * 0.2270270270;
	sum += texture2D(textureRendered, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.1945945946;
	sum += texture2D(textureRendered, vec2(tc.x + 2.0*blur*hstep, tc.y + 2.0*blur*vstep)) * 0.1216216216;
	sum += texture2D(textureRendered, vec2(tc.x + 3.0*blur*hstep, tc.y + 3.0*blur*vstep)) * 0.0540540541;
	sum += texture2D(textureRendered, vec2(tc.x + 4.0*blur*hstep, tc.y + 4.0*blur*vstep)) * 0.0162162162;



	vec4 depth = texture (textureDepth,texCoord);
	vec4 textureColor = texture(textureRendered, texCoord);
	vec4 textureColorLeft = texture(textureRendered,vec2((texCoord.x-1)/width, texCoord.y));
	vec4 textureColorRight = texture(textureRendered,vec2((texCoord.x+1)/width, texCoord.y));
	vec4 textureColorUp = texture(textureRendered,vec2(texCoord.x, (texCoord.y+1)/height));
	vec4 textureColorDown = texture(textureRendered,vec2(texCoord.x, (texCoord.y-1)/height));


//if(depth.g>0.5){

	finalColor = (textureColor*4f+textureColorUp)/6f;
	finalColor = (textureColor*4f+textureColorDown)/6f;
	finalColor = (textureColor*4f+textureColorLeft)/6f;
	finalColor = (textureColor*4f+textureColorRight)/6f;///6f;
	//outColor=finalColor;
	outColor=textureColor*vec4(sum.rgb,1.0);


//}

//	if (gl_FragCoord.y < 200) {//promenna pro cast obrazovky
//		float grey = textureColor.r * 0.33 + textureColor.g * 0.33 + textureColor.b * 0.33;
//		outColor = vec4(grey, grey, grey, 1);
//	} else {
//else		outColor=textureColor;
//
//	}
//	outColor=vec4(depth.b,0,0,1);

}
