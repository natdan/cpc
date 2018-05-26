package org.ah.libgdx.font;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;

public class FontStillModel extends ModelInstance {

    private int spacing = 100;
    private int kern = 0;
    private char c;

    public FontStillModel(char c, ModelData subMeshes) {
        super(new Model(subMeshes));
        this.c = c;
    }

    public char getLetter() {
        return c;
    }

    public int getSpacing() {
        return spacing;
    }

    protected void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public int getKern() {
        return kern;
    }

    protected void setKern(int kern) {
        this.kern = kern;
    }

}
