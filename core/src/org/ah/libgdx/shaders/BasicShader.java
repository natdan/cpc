package org.ah.libgdx.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;



public class BasicShader extends AbstractLightShader {

    private Vector3 scale = new Vector3(1f, 1f, 1f);

    
    public BasicShader() {
        super("data/shaders/basic");
    }
    
    public Vector3 getScale() {
        return scale;
    }
    
    public void setScale(Vector3 scale) {
        this.scale = scale;
    }
    
    public void begin(Camera camera, Matrix4 model) {
        getMaterial().getTexture().bind();
        super.begin();

        shaderProgram.setUniformMatrix("ProjectionMatrix", camera.projection); // mat4
        shaderProgram.setUniformMatrix("ViewMatrix", camera.view); // mat4
        shaderProgram.setUniformMatrix("ModelMatrix", model); // mat4
        shaderProgram.setUniformf("ModelScale", scale); // vec3

        shaderProgram.begin();
    }

    public void setupLights() {
        int numOfLights = Math.min(lightSources.size(), 4);
        
        int location = shaderProgram.getUniformLocation("NumLight");
        shaderProgram.setUniformi(location, numOfLights);
        
        for (int i = 0; i < numOfLights; i++) {
            LightSource lightSource = lightSources.get(i);
            location = shaderProgram.getUniformLocation("Light[" + i + "].Position");
            shaderProgram.setUniform3fv(location, lightSource.getPosition(), 0, 3);
            
            location = shaderProgram.getUniformLocation("Light[" + i + "].Attenuation");
            shaderProgram.setUniform3fv(location, lightSource.getAttenuation(), 0, 3);
            
            location = shaderProgram.getUniformLocation("Light[" + i + "].Direction");
            shaderProgram.setUniform3fv(location, lightSource.getDirection(), 0, 3);
            
            location = shaderProgram.getUniformLocation("Light[" + i + "].Colour");
            shaderProgram.setUniform3fv(location, lightSource.getColour(), 0, 3);
            
            location = shaderProgram.getUniformLocation("Light[" + i + "].OuterCutoff");
            shaderProgram.setUniformf(location, lightSource.getOuterCutoff());
            
            location = shaderProgram.getUniformLocation("Light[" + i + "].InnerCutoff");
            shaderProgram.setUniformf(location, lightSource.getInnerCutoff());
            
            location = shaderProgram.getUniformLocation("Light[" + i + "].Exponent");
            shaderProgram.setUniformf(location, lightSource.getExponent());
        }
    }
    
    public void setupMaterial() {
        int location = shaderProgram.getUniformLocation("Material.Ambient");
        shaderProgram.setUniform3fv(location, material.getAmbient(), 0, 3);
        
        location = shaderProgram.getUniformLocation("Material.Diffuse");
        shaderProgram.setUniform3fv(location, material.getDiffuse(), 0, 3);
        
        location = shaderProgram.getUniformLocation("Material.DiffuseW");
        shaderProgram.setUniformf(location, material.getDiffuseW());
        
        location = shaderProgram.getUniformLocation("Material.Specular");
        shaderProgram.setUniform3fv(location, material.getSpecular(), 0, 3);
        
        location = shaderProgram.getUniformLocation("Material.Shininess");
        shaderProgram.setUniformf(location, material.getShininess());
        
        location = shaderProgram.getUniformLocation("Material.TextureOffset");
        shaderProgram.setUniform2fv(location, material.getTextureOffset(), 0, 2);
        
        location = shaderProgram.getUniformLocation("Material.TextureScale");
        shaderProgram.setUniform2fv(location, material.getTextureScale(), 0, 2);
    }
}
