package org.ah.libgdx.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;


public class DepthShader extends AbstractShader {

    private Vector3 scale = new Vector3(1f, 1f, 1f);
    private float near;
    private float far;
    private float factor;
    
    public DepthShader() {
        super("data/shaders/depth");
    }
    
    public Vector3 getScale() {
        return scale;
    }
    
    public void setScale(Vector3 scale) {
        this.scale = scale;
    }

    public void begin(Camera camera, Matrix4 model) {
        super.begin();
        
        shaderProgram.setUniformMatrix("u_projection_matrix", camera.projection); // mat4
        shaderProgram.setUniformMatrix("u_view_matrix", camera.view); // mat4
        shaderProgram.setUniformMatrix("u_model_matrix", model); // mat4
        shaderProgram.setUniformf("u_model_scale", scale); // vec3
        shaderProgram.setUniformf("u_near", near);
        shaderProgram.setUniformf("u_far", far);
        shaderProgram.setUniformf("u_factor", factor);

        shaderProgram.begin();
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }
}
