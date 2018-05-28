package org.ah.libgdx.font;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.ah.libgdx.shaders.AbstractShader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TDFont {

    public static interface Callback<T> {

        ShaderProgram getShader();

        void begin(Camera cam);

        void render(FontStillModel model);

        Matrix4 calculate(T internal, int spacing, int kern);

        ModelBatch getModelBatch();

        void end();
    }

    public static abstract class AbstractCallback<T> implements Callback<T> {

        protected AbstractShader shader;
        protected ModelBatch modelBatch = new ModelBatch();

        public AbstractCallback(AbstractShader shader) {
            this.shader = shader;
        }

        @Override
        public ShaderProgram getShader() {
            return shader.getShaderProgram();
        }

        @Override
        public void render(FontStillModel model) {
            getModelBatch().render(model);
        }

        @Override
        public void end() {
            modelBatch.end();
            if (shader != null) { shader.getShaderProgram().end(); }
        }

        @Override
        public void begin(Camera cam) {
            if (shader != null) { shader.begin(cam); }
            modelBatch.begin(cam);
        }

        @Override
        public ModelBatch getModelBatch() {
            return modelBatch;
        }

    }

    public static class LinearXYCallback extends AbstractCallback<Matrix4> {

        protected Matrix4 temp = new Matrix4();
        protected float scale;

        public LinearXYCallback(AbstractShader shader, float scale) {
            super(shader);
            this.scale = scale;
        }

        @Override
        public Matrix4 calculate(Matrix4 position, int spacing, int kern) {
            position.translate(spacing * scale, 0, 0);
            temp.set(position);
            temp.translate(0, kern * scale, 0);
            return temp;
        }

    }

    public static double TO_DEGREES = 180 / Math.PI;

    public static class SphericalCallback extends AbstractCallback<Vector3> {

        private Matrix4 temp = new Matrix4();
        private float scale;
        private float sphereRadius;

        public SphericalCallback(AbstractShader shader, float sphereRadius, float scale) {
            super(shader);
            this.scale = scale;
            this.sphereRadius = sphereRadius;
        }

        @Override
        public Matrix4 calculate(Vector3 position, int spacing, int kern) {

            position.set(position.x - spacing * scale / sphereRadius, position.y, position.z);

            temp.idt();
            temp.scale(0.1f, 0.1f, 0.1f);
            temp.rotate(1, 0, 0, 180);
            temp.rotate(0, 1, 0, (float)(position.x * TO_DEGREES))
                .rotate(1, 0, 0, (float)((position.y + (kern * scale  / sphereRadius)) * TO_DEGREES))
                .rotate(0, 0, 1, (float)(position.z * TO_DEGREES));

            temp.translate(0, 0, sphereRadius);

            return temp;
        }

    }

    protected FontStillModel[] letters = new FontStillModel[96];
    protected BufferedReader reader;
    protected char lastLoadedChar;
    protected FontObjLoader objLoader;
    protected FileHandle loadingPath;

    public TDFont() {
    }

    public int getKern(char c) {
        if (c >= ' ' && c < 127) {
            FontStillModel model = letters[c - ' '];
            if (model != null) {
                return model.getKern();
            }
        }
        return -1;
    }

    public void setKern(char c, int k) {
        if (c >= ' ' && c < 127) {
            FontStillModel model = letters[c - ' '];
            if (model != null) {
                model.setKern(k);
            }
        }
    }

    public int getSpacing(char c) {
        if (c >= ' ' && c < 127) {
            FontStillModel model = letters[c - ' '];
            if (model != null) {
                return model.getSpacing();
            }
        }
        return -1;
    }

    public void setSpacing(char c, int s) {
        if (c >= ' ' && c < 127) {
            FontStillModel model = letters[c - ' '];
            if (model != null) {
                model.setSpacing(s);
            }
        }
    }

    public FontStillModel[] getLetters() {
        return letters;
    }

    public void dispose() {
        for (ModelInstance model : letters) {
            if (model != null) {
                model.model.dispose();
            }
        }
    }

    protected String path(String path) {
        int i = path.lastIndexOf('/');
        if (i >= 0) {
            path = path.substring(0, i + 1);
        } else {
            path = "";
        }
        return path;
    }

    public void loadFont(String path) {
        FileHandle file = Gdx.files.internal(path);
        loadFont(file);
    }

    public void loadFont(FileHandle file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 4096);
            try {
                FontObjLoader objLoader = new FontObjLoader();
                FontStillModel model = objLoader.loadObj(file, reader, true);
                while (model != null) {
                    letters[model.getLetter() - 32] = model;
                    model = objLoader.loadObj(file, reader, false);
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            throw new GdxRuntimeException("Cannot load font; " + file, e);
        }
    }

    public void startLoadingFont(String fontFileName) {
        FileHandle file = Gdx.files.internal(fontFileName);
        startLoadingFont(file);
    }

    public void startLoadingFont(FileHandle file) {
        loadingPath = file;
        reader = new BufferedReader(new InputStreamReader(file.read()), 4096);
        objLoader = new FontObjLoader();
    }

    public boolean loadNextChar() {
        try {
            FontStillModel model = objLoader.loadObj(loadingPath, reader, false);
            if (model != null) {
                letters[model.getLetter() - 32] = model;
                lastLoadedChar = model.getLetter();
                return true;
            } else {
                reader.close();
                objLoader = null;
                return false;
            }
        } catch (IOException e) {
            throw new GdxRuntimeException("Cannot load font; " + loadingPath, e);
        }
    }

    public void loadLetters(String path) {
        FontObjLoader objLoader = new FontObjLoader();
        for (int i = 0x21; i <= 0x7E; i++) {
            FontStillModel letter = objLoader.loadObj(Gdx.files.internal(path + "/charHex" + Integer.toHexString(i).toUpperCase() + ".obj"));
            letters[letter.getLetter() - 32] = letter;
        }
    }


    public <T> T drawTextLine(Callback<T> callback, T initialSet, Camera camera, String s) {

        callback.begin(camera);

        boolean first = true;

        int lastSpacing = 0;

        for (int i = 0; i < s.length(); i ++) {
            char c = s.charAt(i);
            if (c >= 127 || c < 32) { c = '?'; }

            int thisSpacing = 100;
            int kern = 100;
            FontStillModel letter = letters[c - 32];
            if (letter != null) {

                thisSpacing = letter.getSpacing();
                kern = letter.getKern();

                int spacing = 0;
                if (first) {
                    first = false;
                    spacing = thisSpacing / 2;
                } else {
                    spacing = lastSpacing / 2 + thisSpacing / 2;
                }

                Matrix4 modelTransform = callback.calculate(initialSet, spacing, kern);

                if (c > ' ' && letters[c - 32] != null) {
                    FontStillModel fontStillModel = letters[c - 32];
                    fontStillModel.transform = modelTransform;
                    callback.render(fontStillModel);
                }
            } else {
                callback.calculate(initialSet, lastSpacing / 2, -1);
            }
            lastSpacing = thisSpacing;
        }
        callback.end();

        callback.calculate(initialSet, lastSpacing / 2, -1);

        return initialSet;
    }

}
