package org.ah.libgdx.shaders;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractLightShader extends AbstractShader {

    protected List<LightSource> lightSources = new ArrayList<LightSource>();
    protected MaterialSource material;
    protected String shaderName;

    public AbstractLightShader(String shaderName) {
        super(shaderName);
    }

    public void addLightSource(LightSource lightSource) {
        this.lightSources.add(lightSource);
    }
    
    public void setMaterial(MaterialSource material) {
        this.material = material;
    }
    
    public MaterialSource getMaterial() {
        return material;
    }

    public abstract void setupLights();
    
    public abstract void setupMaterial();
}
