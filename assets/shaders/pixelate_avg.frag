#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define PIXEL_SIZE 5.0

// I use vertex colors to pass various per-entity parameters.
// For this shader it's unused;
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_worldSize;

void main() {
    vec2 xy = floor(u_worldSize * v_texCoords);
    xy = xy - mod(xy, PIXEL_SIZE);
    vec2 uv = xy / u_worldSize;
    float dx = 1.0 / u_worldSize.x;
    float dy = 1.0 / u_worldSize.y;
    vec4 pixelSample = vec4(0.0, 0.0, 0.0, 0.0);

    for (float x = 0.0; x < PIXEL_SIZE; x++) {
        for (float y = 0.0; y < PIXEL_SIZE; y++) {
            pixelSample += texture2D(u_texture, uv + vec2(dx * x, dy * y));
        }
    }

    gl_FragColor = vec4(pixelSample.rgb / (PIXEL_SIZE * PIXEL_SIZE), 1.0);
}
