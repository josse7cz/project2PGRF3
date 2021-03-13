#version 150
in vec2 texCoord;

uniform sampler2D textureMosaic;

out vec4 outColor; // output from the fragment shader

void main() {
	vec4 textureColor = texture(textureMosaic, texCoord);

//	outColor = vec4(1.0, 0.0, 0.0, 1.0);
	outColor = textureColor;
} 
