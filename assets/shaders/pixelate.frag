#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define DARKENING 0.6
#define PIXEL_SIZE 2.0

// I use vertex colors to pass various parameters.
// For this shader it's unused;
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_worldSize;

void main() {
    vec2 xy = round(u_worldSize * v_texCoords);
    xy = xy - mod(xy, PIXEL_SIZE);
    vec2 uv = xy / u_worldSize;
    vec4 outColor = texture2D(u_texture, uv);
    gl_FragColor = vec4(
        outColor.r * DARKENING,
        outColor.g * DARKENING,
        outColor.b * DARKENING,
        outColor.a
    );
}
