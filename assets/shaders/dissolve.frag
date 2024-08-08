#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

// I use vertex colors to pass various per-entity parameters.
// For this shader, r channel is noise scale, g channel is normalized remaining time, b channel is theme selection.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;

// *******************************************************************************
// Noise function: https://gamedev.stackexchange.com/a/166064 by Felipe Gutierrez
// *******************************************************************************

float hash(vec2 v) {
    return fract(sin(v.x * 3433.8 + v.y * 3843.98) * 45933.8);
}

// Value noise courtesy of BigWingz
// check his youtube channel he has
// a video of this one.
// Succint version by FabriceNeyret
float fnoise(vec2 v) {
    vec2 id = floor(v);
    v = fract(v);
    v *= v * (3.0 - 2.0 * v);

    vec2 a = vec2(
        hash(id),
        hash(id + vec2(0.0, 1.0))
    );
    vec2 b = vec2(
        hash(id + vec2(1.0, 0.0)),
        hash(id + vec2(1.0, 1.0))
    );
    vec2 c = mix( a, b, v.x);

    return mix(c.x, c.y, v.y);
}

// Alternative
float frand2(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float fnoise2(vec2 n) {
    const vec2 d = vec2(0.0, 1.0);
    vec2 b = floor(n);
    vec2 f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
    return mix(
        mix(frand2(b), frand2(b + d.yx), f.x),
        mix(frand2(b + d.xy), frand2(b + d.yy), f.x), f.y);
}

// *******************************************************************************
// End of the noise function
// *******************************************************************************

void main() {
    vec4 outColor = texture2D(u_texture, v_texCoords);
    float remainingTime = v_color.g;
    float scale = v_color.r;
    float n = fnoise2(vec2(v_texCoords.x * scale * 800.0, v_texCoords.y * scale * 200.0));
    float alpha =  smoothstep(0.6, 0.9, min(n*n + remainingTime, 1.0));
    // Burnt edges:
    vec3 tint = mix(vec3(0.8, 0.4,  0.2), vec3(0.5, 0.2,  0.3), v_color.b);
    outColor.rgb = mix(tint, outColor.rgb, alpha);

    gl_FragColor = vec4(outColor.rgb, outColor.a * alpha);
}