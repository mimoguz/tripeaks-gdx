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
    vec4 outColor = texture2D(u_texture, v_texCoords)
        + texture2D(u_texture, v_texCoords + vec2(0.0, 0.005))
        + texture2D(u_texture, v_texCoords + vec2(0.003, 0.005))
        + texture2D(u_texture, v_texCoords + vec2(0.003, 0.0))
        + texture2D(u_texture, v_texCoords + vec2(0.003, -0.005))
        + texture2D(u_texture, v_texCoords + vec2(0.0, -0.005))
        + texture2D(u_texture, v_texCoords + vec2(-0.003, -0.005))
        + texture2D(u_texture, v_texCoords + vec2(-0.003, 0.0))
        + texture2D(u_texture, v_texCoords + vec2(-0.003, 0.005));
    outColor = outColor / 9.0;
    float t = 1.0 - v_color.g;
    float x = t * 2.1 + 0.5;
    float alpha =  (sin(4.0 * x) + cos(x) + 1.5) / 3.2;
    vec3 tint = mix(vec3(v_color.g, t, 0.0), vec3(v_color.g * 0.5 + 0.1, 0.0, t * 0.5), v_color.b);
    vec3 rgb = mix(tint, outColor.rgb, alpha);
    alpha = clamp(0.0, 1.0, alpha);
    gl_FragColor = vec4(rgb, outColor.a * alpha);
}
