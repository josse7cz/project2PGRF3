#version 150
in vec2 texCoord;//souradnice do textury
uniform sampler2D textureRendered;
uniform sampler2D textureDepth;
uniform float width;
uniform float height;
out vec4 outColor; // output from the fragment shader
vec4 finalColor;
vec2 blurTextureCoord[11];



void main() {
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
	finalColor = (textureColor*4f+textureColorRight)/6f;


outColor+=finalColor;

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
