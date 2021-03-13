#version 150
in vec2 texCoord;

uniform sampler2D textureRendered;

out vec4 outColor; // output from the fragment shader

void main() {
	vec4 textureColor = texture(textureRendered, texCoord);

	if (gl_FragCoord.y < 200) {
		float grey = textureColor.r * 0.33 + textureColor.g * 0.33 + textureColor.b * 0.33;
		outColor = vec4(grey, grey, grey, 1);
	} else {
		outColor = textureColor;
	}
} 
