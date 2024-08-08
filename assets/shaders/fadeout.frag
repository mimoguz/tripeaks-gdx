#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

// I use vertex colors to pass various per-entity parameters.
// For this shader, g channel is normalized remaining time.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    float t = v_color.g;
    float alpha =  t * t * t;
    gl_FragColor = vec4(color.rgb, color.a * alpha);
}
