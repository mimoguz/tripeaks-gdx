#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define MAX_WIDTH 6.0

// I use vertex colors to pass various per-entity parameters.
// For this shader, g channel is normalized remaining time.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_worldSize;

void main() {
//    float t = v_color.g;
//    float width = MAX_WIDTH * (1.0 - t) + 1.0;
//    float halfWidth = width * 0.5;
//    vec4 pixelSample = vec4(0.0, 0.0, 0.0, 0.0);
//    float step = 1.0 / MAX_WIDTH;
//    float samples = floor(width / step + 0.5);
//    vec2 xy = v_texCoords + vec2(halfWidth, halfWidth);
//    for (float x = -halfWidth; x < halfWidth; x += step) {
//        for (float y = -halfWidth; y < halfWidth; y += step) {
//            vec2 uv = xy + vec2(x, y);
//            pixelSample += texture2D(u_texture, uv);
//        }
//    }
//    gl_FragColor = mix(
//        vec4(pixelSample.rgb / (samples * samples), t),
//        texture2D(u_texture, v_texCoords),
//        step(0.9, t)
//    );

    float size = (1.0 - v_color.g) * MAX_WIDTH;
    vec2 xy = floor(vec2(21.0, 32.0) * v_texCoords / size) * size;
    vec2 uv = xy / vec2(21.0, 32.0);
    vec4 color = texture2D(u_texture, uv);
    color.a = v_color.g;
    gl_FragColor = mix(
        color,
        texture2D(u_texture, v_texCoords),
        step(0.99, v_color.g)
    );
}
