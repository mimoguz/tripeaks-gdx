#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define SAMPLES 6.0
#define STRIDE 4.0

// I use vertex colors to pass various per-entity parameters.
// For this shader it's unused;
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_worldSize;

void main() {
    vec2 xy = floor(u_worldSize * v_texCoords / (SAMPLES * STRIDE)) * (SAMPLES * STRIDE);
    vec4 pixelSample = vec4(0.0, 0.0, 0.0, 0.0);
    for (float x = 0.0; x < SAMPLES; x += 1.0) {
        for (float y = 0.0; y < SAMPLES; y += 1.0) {
            vec2 uv = vec2((xy.x + x * STRIDE) / u_worldSize.x, (xy.y + y * STRIDE) / u_worldSize.y);
            pixelSample += texture2D(u_texture, uv);
        }
    }
    gl_FragColor = vec4(pixelSample.rgb / (SAMPLES * SAMPLES), 1.0);
}
