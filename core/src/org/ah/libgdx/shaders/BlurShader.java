package org.ah.libgdx.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;


public class BlurShader extends AbstractShader {

    private int orientation;
    private Texture colorTexture;
    private Texture depthTexture;
    private int width;
    private int height;
    private float[] texel = new float[2];
   
    
    public BlurShader(int orientation) {
        super("data/shaders/image:data/shaders/dof_blur");
//        super("data/shaders/image");
        this.orientation = orientation;
    }
    
    public void begin() {
        
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
        colorTexture.bind();
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE1);
        depthTexture.bind();
        
        super.begin();
        
        shaderProgram.begin();
        shaderProgram.setUniformi("u_orientation", orientation);
        shaderProgram.setUniform2fv("u_texel_size", texel, 0, 2); // vec2
        shaderProgram.setUniformi("u_colour_texture0", 0);
        shaderProgram.setUniformi("u_depth_texture1", 1);
        
    }

    public Texture getColorTexture() {
        return colorTexture;
    }

    public void setColorTexture(Texture colorTexture) {
        this.colorTexture = colorTexture;
    }

    public Texture getDepthTexture() {
        return depthTexture;
    }

    public void setDepthTexture(Texture depthTexture) {
        this.depthTexture = depthTexture;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        texel[0] = 1f / height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        texel[1] = 1f / height;
    }
}
