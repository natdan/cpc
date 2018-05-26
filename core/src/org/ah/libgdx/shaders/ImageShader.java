package org.ah.libgdx.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;


public class ImageShader extends AbstractShader {

    private Texture colorTexture;
    private Texture blurTexture;
   
    
    public ImageShader() {
        super("data/shaders/image");
    }
    
    public void begin() {
        
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
        colorTexture.bind();
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE1);
        blurTexture.bind();
        
        super.begin();
        
        shaderProgram.setUniformi("u_color_texture0", 0);
        shaderProgram.setUniformi("u_blur_texture1", 1);

        shaderProgram.begin();
    }

    public Texture getColorTexture() {
        return colorTexture;
    }

    public void setColorTexture(Texture colorTexture) {
        this.colorTexture = colorTexture;
    }

    public Texture getBlurTexture() {
        return blurTexture;
    }

    public void setBlurTexture(Texture blurTexture) {
        this.blurTexture = blurTexture;
    }

}
