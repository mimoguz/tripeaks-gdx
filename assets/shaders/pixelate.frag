#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define SAMPLES 6.0
#define STRIDE 4.0

// I use vertex colors to pass various per-entity parameters.
// For this shader, r channel is normalized time.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_worldSize;

void main() {
    float stride = max(floor(v_color.r * STRIDE), 0.001);
    float samples = SAMPLES;//max(floor(v_color.r * SAMPLES), 1.0);
    vec2 pos = floor(u_worldSize * v_texCoords / (samples * stride)) * (samples * stride);
    vec4 pixelSample = vec4(0.0, 0.0, 0.0, 0.0);
    for (float x = 0.0; x < samples; x += 1.0) {
        for (float y = 0.0; y < samples; y += 1.0) {
            vec2 uv =  (pos +  stride * vec2(x, y)) / u_worldSize;
            pixelSample += texture2D(u_texture, uv);
        }
    }
    gl_FragColor = vec4(pixelSample.rgb / (samples * samples), 1.0);
}
