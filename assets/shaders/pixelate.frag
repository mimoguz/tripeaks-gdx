#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define PIXEL_SIZE 4.0

// I use vertex colors to pass various per-entity parameters.
// For this shader it's unused;
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_worldSize;

void main() {
    vec2 xy = floor(u_worldSize * v_texCoords);
    xy = xy + vec2(1.0, 1.0) - mod(xy, PIXEL_SIZE);
    vec2 uv = xy / u_worldSize;
    gl_FragColor = texture2D(u_texture, uv);
}
