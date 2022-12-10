#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define PIXEL_DENSITY 128.0

// I use vertex colors to pass various parameters.
// For this shader, r channel is aspect ratio.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
    vec2 pixelScaling = vec2(PIXEL_DENSITY, PIXEL_DENSITY  * v_color.r);
    vec2 uv = round(v_texCoords * pixelScaling) / pixelScaling;
    vec4 outColor = texture2D(u_texture, uv);
    gl_FragColor = vec4(
        outColor.r * 0.6,
        outColor.g * 0.6,
        outColor.b * 0.6,
        outColor.a
    );
}
