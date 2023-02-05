#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define CENTER_CONT 0.2
// 0.8 / 8 = 0.1
#define NEIGHBOR_CONT 0.1
#define DARKENING 1.0

// I use vertex colors to pass various per-entity parameters.
// For this shader it's unused.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_worldSize;

void main() {
    vec4 outColor = texture2D(u_texture, v_texCoords);
    float dx = 1.0 / u_worldSize.x;
    float dy = 1.0 / u_worldSize.y;
    vec4 blurSample =
        texture2D(u_texture, v_texCoords + vec2(0.0, dy))     // North
        + texture2D(u_texture, v_texCoords + vec2(dx, 0.0))   // Eeast
        + texture2D(u_texture, v_texCoords + vec2(0.0, -dy))  // South
        + texture2D(u_texture, v_texCoords + vec2(-dx, 0.0)); // West
        + texture2D(u_texture, v_texCoords + vec2(dx, dy))    // North-east
        + texture2D(u_texture, v_texCoords + vec2(dx, -dy))   // North-west
        + texture2D(u_texture, v_texCoords + vec2(-dx, dy))   // South-east
        + texture2D(u_texture, v_texCoords + vec2(-dx, -dy)); // South-west
    gl_FragColor = vec4(
        outColor.rgb * (CENTER_CONT * DARKENING) + blurSample.rgb * (NEIGHBOR_CONT * DARKENING),
        outColor.a * CENTER_CONT + blurSample.a * NEIGHBOR_CONT
    );
}
