#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define DX 0.003
#define DY 0.005
#define CENTER_CONT 0.25
// 0.75 / 4 = 0.1875
#define NEIGHBOR_CONT 0.1875
#define DARKENING *.6

// I use vertex colors to pass various parameters.
// For this shader it's unused.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
    vec4 outColor = texture2D(u_texture, v_texCoords);
    vec4 blurSample =
        texture2D(u_texture, v_texCoords + vec2(0.0, DY))     // North
        + texture2D(u_texture, v_texCoords + vec2(DX, 0.0))   // Eeast
        + texture2D(u_texture, v_texCoords + vec2(0.0, -DY))  // South
        + texture2D(u_texture, v_texCoords + vec2(-DX, 0.0)); // West
    gl_FragColor = vec4(
        (outColor.r * CENTER_CONT + blurSample.r * NEIGHBOR_CONT) * DARKENING,
        (outColor.g * CENTER_CONT + blurSample.g * NEIGHBOR_CONT) * DARKENING,
        (outColor.b * CENTER_CONT + blurSample.b * NEIGHBOR_CONT) * DARKENING,
        outColor.a * CENTER_CONT + blurSample.a * NEIGHBOR_CONT
    );
}
