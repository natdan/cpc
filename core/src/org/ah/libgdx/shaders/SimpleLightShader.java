package org.ah.libgdx.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;

public class SimpleLightShader extends AbstractLightShader {

    Matrix4 transform = new Matrix4();

    public SimpleLightShader() {
        super("data/shaders/light-color");
    }
    
    public void begin(Camera camera, Matrix4 model) {
        super.begin();

        transform.set(camera.combined);
        transform.mul(model);

        shaderProgram.setUniformMatrix("u_projView", transform);
        shaderProgram.setUniform3fv("u_light_direction", lightSources.get(0).getDirection(), 0, 3);
        shaderProgram.setUniform3fv("u_diffuse_color", material.getDiffuse(), 0, 3);
    }
    
    public void setupLights() {
//        shaderProgram.setUniform3fv("u_light_direction", lightSources.get(0).getDirection(), 0, 3);
    }
    
    public void setupMaterial() {
//        shaderProgram.setUniform3fv("u_diffuse_color", material.getDiffuse(), 0, 3);
    }
}
