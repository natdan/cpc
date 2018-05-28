#ifdef GL_ES 
precision mediump float;
#endif

uniform sampler2D u_grass;
uniform sampler2D u_extra;
uniform float u_time;
uniform float u_layer;

varying vec2 v_texCoord0;

void main() {
    float shadow = mix(0.4, 1.0, u_layer);
    vec4 grass = texture2D(u_grass, v_texCoord0);

    gl_FragColor = texture2D(u_extra, v_texCoord0) * shadow * grass.a;
}
