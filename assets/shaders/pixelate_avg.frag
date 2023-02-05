#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define PIXEL_SIZE 2.0

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
    vec4 pixelSample =
        texture2D(u_texture, uv)
        + texture2D(u_texture, uv + vec2(dx, 0))     // right
        + texture2D(u_texture, uv + vec2(dx, dy))    // down-right
        + texture2D(u_texture, uv + vec2(0.0, dy));  // down
    gl_FragColor = vec4(pixelSample.rgb * 0.25, 1.0);
}
