#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

/* ************************* NOISE FUNCTION **************************** */

vec3 permute(vec3 x) { return mod(((x*34.0)+1.0)*x, 289.0); }

float snoise(vec2 v){
    const vec4 C = vec4(0.211324865405187, 0.366025403784439,
                        -0.577350269189626, 0.024390243902439);
    vec2 i  = floor(v + dot(v, C.yy) );
    vec2 x0 = v -   i + dot(i, C.xx);
    vec2 i1;
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;
    i = mod(i, 289.0);
    vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
                      + i.x + vec3(0.0, i1.x, 1.0 ));
    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy),
                            dot(x12.zw,x12.zw)), 0.0);
    m = m*m ;
    m = m*m ;
    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;
    m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

float fnoise(vec2 pos) {
    return snoise(pos);
}

/* *********************** END NOISE FUNCTION ************************** */

// I use vertex colors to pass various per-entity parameters.
// For this shader, r channel is aspect ratio, g channel is normalized remaining time,
varying LOWP vec4 v_color;

varying vec2 v_texCoords;

uniform sampler2D u_texture;

#define WORLD_HEIGHT 168.0
void main() {
    vec4 outColor = texture2D(u_texture, v_texCoords);
    float remainingTime = v_color.g;
    float scale = v_color.r / v_color.b;
    vec2 worldSize = vec2(floor(WORLD_HEIGHT * v_color.b), WORLD_HEIGHT);
    vec2 pos = floor(v_texCoords * worldSize);
    float n = 1.0 - fnoise(pos);
    float alpha = outColor.a * (1.0 - step(min(n * n * remainingTime, 1.0), 0.9));
    vec4 tint = vec4(0.7216, 0.2157, 0.2667, alpha);
    gl_FragColor = mix(vec4(outColor.rgb, alpha), tint, 0.1) ;
}
