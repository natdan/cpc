package org.ah.libgdx.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;


public abstract class AbstractShader {

    protected ShaderProgram shaderProgram;
    protected String shaderName;

    public AbstractShader(String shaderName) {
        this.shaderName = shaderName;
    }

    public void init() {
        int separator = shaderName.indexOf(':');
        if (separator < 0) {
            shaderProgram = new ShaderProgram(Gdx.files.internal(shaderName + ".vs"), Gdx.files.internal(shaderName + ".fs"));
        } else {
            shaderProgram = new ShaderProgram(Gdx.files.internal(shaderName.substring(0, separator) + ".vs"), Gdx.files.internal(shaderName.substring(separator + 1) + ".fs"));
        }
        if (!shaderProgram.isCompiled()) throw new GdxRuntimeException("Couldn't compile " + shaderName + " shader\n" + shaderProgram.getLog());
    }

    public void dispose() {
        shaderProgram.dispose();
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public void begin(Camera camera) {
        begin();
    }

    public void begin() {
        shaderProgram.begin();
    }

    public void end() {
        shaderProgram.end();
    }
}
