package org.ah.libgdx.shaders;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class MaterialSource {
    float[] ambient = new float[4];
    float[] diffuse = new float[3];
    float diffuseW = 0f;
    float[] specular = new float[3];
    float shininess = 0f;
    float[] textureOffset = new float[2];
    float[] textureScale = new float[2];
    Texture texture;
    
    public MaterialSource() {
        textureScale[0] = 1.0f;
        textureScale[1] = 1.0f;
        ambient[3] = 1.0f;
    }
    
    public float[] getAmbient() {
        return ambient;
    }
    
    public void setAmbient(float r, float g, float b) {
        ambient[0] = r;
        ambient[1] = g;
        ambient[2] = b;
    }
    
    public float[] getDiffuse() {
        return diffuse;
    }
    
    public void setDiffuse(float r, float g, float b, float w) {
        diffuse[0] = r;
        diffuse[1] = g;
        diffuse[2] = b;
        diffuseW = w;
    }
    
    public float getDiffuseW() {
        return diffuseW;
    }
    
    public float[] getSpecular() {
        return specular;
    }
    
    public void setSpecular(float r, float g, float b) {
        specular[0] = r;
        specular[1] = g;
        specular[2] = b;
    }
    
    public float getShininess() {
        return shininess;
    }
    
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
    
    public float[] getTextureOffset() {
        return textureOffset;
    }
    
    public void setTextureOffset(float x, float y) {
        textureOffset[0] = x;
        textureOffset[1] = y;
    }
    
    public float[] getTextureScale() {
        return textureScale;
    }
    
    public void setTextureScale(float x, float y) {
        textureScale[0] = x;
        textureScale[1] = y;
    }
    
    public Texture getTexture() {
        if (texture == null) {
            Pixmap pixmap = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
            pixmap.setColor(0xffffff);
            pixmap.fill();
            texture = new Texture(pixmap);
        }
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}