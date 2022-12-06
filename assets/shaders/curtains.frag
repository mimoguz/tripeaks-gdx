#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

// I use that to pass various attributes.
// Currently, r channel is wave period, and g channel is normalized remaining time.
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;


// *******************************************************************************
// End of the noise function
// *******************************************************************************

void main() {
    vec4 outColor = texture2D(u_texture, v_texCoords);
    float t = 1.0 - v_color.g;
    float alpha =  smoothstep(t - 0.5, t, mod(v_texCoords.y, v_color.r) / v_color.r);
    gl_FragColor = vec4(outColor.rgb, outColor.a * alpha);
}
