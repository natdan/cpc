package org.ah.libgdx.shaders;


public class LightSource {
    
    float[] position = new float[3];
    float[] attenuation = new float[3];
    float[] direction = new float[3];
    float[] colour = new float[3];
    float outerCutoff = 0.0f;
    float innerCutoff = 0.0f;
    float exponent = 0.0f;
    
    
    public LightSource() {
        colour[0] = 1.0f;
        colour[1] = 1.0f;
        colour[2] = 1.0f;
    }
    
    public static LightSource point(float x, float y, float z, float attenuation) {
        LightSource lightSource = new LightSource();
        lightSource.position[0] = x;
        lightSource.position[1] = y;
        lightSource.position[2] = z;
        lightSource.attenuation[2] = attenuation;
        return lightSource;
    }
    
    public static LightSource diffuseDirectional(float x, float y, float z) {
        LightSource lightSource = new LightSource();
        lightSource.direction[0] = x;
        lightSource.direction[1] = y;
        lightSource.direction[2] = z;
        return lightSource;
    }

    public float[] getPosition() {
        return position;
    }

    public float[] getAttenuation() {
        return attenuation;
    }

    public float[] getDirection() {
        return direction;
    }

    public float[] getColour() {
        return colour;
    }
    
    public void setColour(float r, float g, float b) {
        colour[0] = r;
        colour[1] = g;
        colour[2] = b;
    }

    public float getOuterCutoff() {
        return outerCutoff;
    }

    public void setOuterCutoff(float outerCutoff) {
        this.outerCutoff = outerCutoff;
    }

    public float getInnerCutoff() {
        return innerCutoff;
    }

    public void setInnerCutoff(float innerCutoff) {
        this.innerCutoff = innerCutoff;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }
    
    
}