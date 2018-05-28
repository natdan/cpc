#ifdef GL_ES 
precision mediump float;
#endif

attribute vec3 a_position;
attribute vec3 a_color;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform float u_layer;
uniform float u_hair_length;
uniform vec3 u_displacement;

varying vec3 v_color;
varying vec3 v_normal;
varying vec2 v_texCoord0;

void main() {
    v_color = a_color;
    v_normal = a_normal;
    v_texCoord0 = a_texCoord0;

    float displacementFactor = pow(u_layer, 3.0);

//    vec3 pos = a_position + u_layer * u_hair_length * a_normal + u_displacement * displacementFactor;
    vec3 pos = a_position + u_layer * u_hair_length * vec3(0.0, 0.0, -1.0) + u_displacement * displacementFactor;

    gl_Position = u_projViewTrans * u_worldTrans * vec4(pos, 1.0);
}
