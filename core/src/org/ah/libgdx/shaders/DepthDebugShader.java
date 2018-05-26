package org.ah.libgdx.shaders;

import com.badlogic.gdx.graphics.Texture;


public class DepthDebugShader extends AbstractShader {

    private Texture colorTexture;
   
    
    public DepthDebugShader() {
        super("data/shaders/image:data/shaders/depth_debug");
    }
    
    public void begin() {
        
        colorTexture.bind(0);
        
        super.begin();

        shaderProgram.begin();
    }

    public Texture getColorTexture() {
        return colorTexture;
    }

    public void setColorTexture(Texture colorTexture) {
        this.colorTexture = colorTexture;
    }

}
