#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

// I use vertex colors to pass various parameters.
// For this shader, g channel is normalized remaining time, b channel is theme selection.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
    vec4 outColor = texture2D(u_texture, v_texCoords);
    vec4 blurSample =
        texture2D(u_texture, v_texCoords + vec2(0.0, 0.005)) // Plus
        //texture2D(u_texture, v_texCoords + vec2(0.002, 0.003)) // Diagonals
        + texture2D(u_texture, v_texCoords + vec2(0.003, 0.0))
        //+ texture2D(u_texture, v_texCoords + vec2(0.002, -0.003))
        + texture2D(u_texture, v_texCoords + vec2(0.0, -0.005))
        //+ texture2D(u_texture, v_texCoords + vec2(-0.002, -0.003))
        + texture2D(u_texture, v_texCoords + vec2(-0.003, 0.0));
        //+ texture2D(u_texture, v_texCoords + vec2(-0.002, 0.003));
    // 0.75 / 4 = 0.1875
    gl_FragColor = vec4(
        (outColor.r * 0.25 + blurSample.r * 0.1875) * 0.6,
        (outColor.g * 0.25 + blurSample.g * 0.1875) * 0.6,
        (outColor.b * 0.25 + blurSample.b * 0.1875) * 0.6,
        outColor.a * 0.25 + blurSample.a * 0.1875
    );
}
