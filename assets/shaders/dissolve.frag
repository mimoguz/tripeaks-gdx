#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

// I use that to pass various attributes.
// Currently, r channel is used for noise scale, and g channel is used for dissolve animation time.
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

// *******************************************************************************
// End of the noise function
// *******************************************************************************

void main() {
    vec4 outColor = texture2D(u_texture, v_texCoords);
    float n = fnoise(vec2(v_texCoords.x * 512.0 * v_color.r, v_texCoords.y  * 256 * v_color.r));
    float alpha =  smoothstep(0.6, 0.9, min(n * n + v_color.g, 1.0));

    // Burnt edges:
    outColor.rgb = mix(vec3(0.8, 0.4,  0.2), outColor.rgb, smoothstep(0.6, 0.9, alpha));

    gl_FragColor = vec4(outColor.rgb, outColor.a * alpha);
}