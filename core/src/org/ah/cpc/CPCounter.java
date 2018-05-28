package org.ah.cpc;

import static org.ah.cpc.CPCConstants.CREATIVE_SPHERE_PNG;
import static org.ah.cpc.CPCConstants.CURSOR_EMPTY_PNG;
import static org.ah.cpc.CPCConstants.CURSOR_PNG;
import static org.ah.cpc.CPCConstants.FONT_32_FNT;
import static org.ah.cpc.CPCConstants.FUR_JPG;
import static org.ah.cpc.PlatformSpecific.CONFIG_IS_DESKTOP;
import static org.ah.cpc.PlatformSpecific.CONFIG_MOUSE_SHOW;

import java.util.Random;

import org.ah.libgdx.font.FontStillModel;
import org.ah.libgdx.font.TDFont;
import org.ah.libgdx.font.TDFont.LinearXYCallback;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class CPCounter extends ApplicationAdapter implements InputProcessor {

    private static final int LAYERS = 30;

    public static final float CLICK_FEEDBACK_TIMEOUT = 1250f;
    // private LibGDXLogger logger = new LibGDXLogger("Frontend");

    private AssetManager assetManager;
    // private AssetTextureProvider textureProvider;
    private boolean loadingAssets = true;

    private PlatformSpecific platformSpecific;

    private Sprite creativeSphereSprite;

    private RenderContext renderContext;
    private SpriteBatch spriteBatch;
    private ModelBatch modelBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera orthographicCamera;
    private PerspectiveCamera perspectiveCamera;

    private BitmapFont smallFont;

    private long startedTime;
    private long lastRendered;
    private int elapsedTime;
    private float furTime;

    private long splashTimeout = 100;

    private Sprite pointer;

    private Mesh backgroundMesh;
    // private ShaderProgram backgroundShaderProgram;
    private Renderable renderable;
    private DefaultShader perlinColourShader;

    private int width;
    private int height;

    private TDFont tdFont;
    private Matrix4 textPosition = new Matrix4();
    private LinearXYCallback linearCallback;
    private InputMultiplexer cameraInputMultiplexer;
    private CameraInputController cameraController;
    private Environment environment;

    private Random random = new Random(1);
    private Material transparentBlueMaterial;
    private DirectionalLight directionalLight;

    private DefaultShader furShader;

    private Texture furExtraTexture;
    private Texture furTexture;

    private Vector3 displacement = new Vector3();
    private Vector3 gravity = new Vector3(0f, 0f, -1.0f);
    private Vector3 forceDirection = Vector3.Zero;

    public CPCounter(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;
    }

    @Override
    public void create() {
        System.out.println("Starting CPC");
        System.out.println();
        if (platformSpecific.getConfigParameterAsBool(CONFIG_IS_DESKTOP)) {
            System.out.println("It is desktop - cursor handing is off!");
        } else {
            System.out.println("Target platform - cursor handing is on!");
        }

        assetManager = new AssetManager();
        // textureProvider = new AssetTextureProvider(assetManager);

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        orthographicCamera = new OrthographicCamera(width, height);
        orthographicCamera.setToOrtho(true, 1280, 800);

        perspectiveCamera = new PerspectiveCamera(45, 1280, 800);
        perspectiveCamera.position.set(5.5f, 1f, 3f);
        perspectiveCamera.up.set(new Vector3(0, 1, 0));
        perspectiveCamera.lookAt(2.5f, 0f, 0f);

        perspectiveCamera.near = 0.02f;
        perspectiveCamera.far = 1000f;
        perspectiveCamera.update();

        cameraController = new CameraInputController(perspectiveCamera);
        cameraController.target.set(new Vector3(2.5f, 0f, 0f));
        cameraInputMultiplexer = new InputMultiplexer();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(cameraController);
        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        // Gdx.input.setInputProcessor(this);

        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(orthographicCamera.combined);
        spriteBatch.enableBlending();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
        shapeRenderer.setAutoShapeType(true);

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

        modelBatch = new ModelBatch(renderContext);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        directionalLight = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        environment.add(directionalLight);

        transparentBlueMaterial = new Material(
                ColorAttribute.createDiffuse(0.0f, 0.0f, 0.8f, 0.8f),
                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
                );

        assetManager.load(CREATIVE_SPHERE_PNG, Texture.class);
        assetManager.load(CURSOR_PNG, Pixmap.class);
        assetManager.load(CURSOR_EMPTY_PNG, Pixmap.class);
        assetManager.load(FUR_JPG, Texture.class);

        BitmapFontParameter bitmapFontParameter = new BitmapFontParameter();
        bitmapFontParameter.flip = true;

        assetManager.load(FONT_32_FNT, BitmapFont.class, bitmapFontParameter);


        lastRendered = System.currentTimeMillis();
        startedTime = lastRendered;

        resize(width, height);
    }

    protected void finishedLoading() {
        loadingAssets = false;

        if (!platformSpecific.getConfigParameterAsBool(CONFIG_IS_DESKTOP)) {
            Pixmap pixmap = new Pixmap(32, 32, Format.RGBA8888);
            if (platformSpecific.getConfigParameterAsBool(CONFIG_MOUSE_SHOW)) {
                pixmap = assetManager.get(CURSOR_EMPTY_PNG);
            } else {
                pixmap = assetManager.get(CURSOR_PNG);
            }
            Cursor customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
            Gdx.graphics.setCursor(customCursor);
        }
        if (platformSpecific.getConfigParameterAsBool(CONFIG_MOUSE_SHOW)) {
            pointer = new Sprite(new Texture(assetManager.get(CURSOR_PNG, Pixmap.class)));
        }

        furExtraTexture = assetManager.get(FUR_JPG);
        furExtraTexture = new Texture(Gdx.files.internal("fur.jpg"));
        furTexture = fillFurTexture(1024, 1024, 0.1f);

        tdFont = new TDFont();
        tdFont.loadFont("arial-3d.font");

        linearCallback = new TDFont.LinearXYCallback(null, 0.015f) {
            @Override public void render(FontStillModel model) {
                Material material = model.materials.get(0);

                material.set(transparentBlueMaterial);
                model.getRenderable(renderable);

                // renderable.material = null;

                furShader.program.setUniformMatrix("u_worldTrans", renderable.worldTransform);

                for (int i = 0; i < LAYERS; i++) {
                    furShader.program.setUniformf("u_layer", (float)i / (float)LAYERS);
                    furShader.render(renderable);
                }


            }

            @Override public Matrix4 calculate(Matrix4 position, int spacing, int kern) {
                Matrix4 result = super.calculate(position, spacing, kern);
//                result.translate(random.nextFloat() * 0.01f, random.nextFloat() * 0.01f, random.nextFloat() * 0.01f);
//                result.translate(random.nextFloat() * 0.005f, random.nextFloat() * 0.005f, random.nextFloat() * 0.005f);

                if (elapsedTime % 120 < 21) {
                    float t = elapsedTime % 120;
                    t = 10f - Math.abs(t - 10f);
                    t = 1f + t / 500f;

                    result.scale(t, t, t);
                }
                result.scale(1f, 1f, 4f);

                return result;
            }

        };

        smallFont = assetManager.get(FONT_32_FNT);

        backgroundMesh = createRect(0, 0, 0.5f, 0.5f);
        renderable = new Renderable();

        renderable.meshPart.mesh = backgroundMesh;
        renderable.meshPart.size = backgroundMesh.getNumIndices();
//        renderable.meshPart.mesh = tdFont.getLetters()['A' - ' '].model.meshes.get(0);
//        renderable.meshPart.size = tdFont.getLetters()['A' - ' '].model.meshes.get(0).getNumIndices();
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;

        renderable.material = null;
        renderable.worldTransform.idt();
//
//        String perlinColourVertexProgram = Gdx.files.internal("shaders/background.vs").readString();
//        String perlinColourFragmentProgram = Gdx.files.internal("shaders/background.fs").readString();
//
//        perlinColourShader = new DefaultShader(renderable, new DefaultShader.Config(perlinColourVertexProgram, perlinColourFragmentProgram));
//        perlinColourShader.init();
//        if (!perlinColourShader.program.isCompiled()) {
//            Gdx.app.log("Shader error: ", perlinColourShader.program.getLog());
//            System.out.println("Shader error" + perlinColourShader.program.getLog());
//            System.exit(-1);
//        }

        String furVertexProgram = Gdx.files.internal("shaders/fur.vs").readString();
        String furFragmentProgram = Gdx.files.internal("shaders/fur.fs").readString();

        furShader = new DefaultShader(renderable, new DefaultShader.Config(furVertexProgram, furFragmentProgram));
        furShader.init();
        if (!furShader.program.isCompiled()) {
            Gdx.app.log("Shader error: ", furShader.program.getLog());
            System.out.println("Shader error" + furShader.program.getLog());
            System.exit(-1);
        }
    }

    @Override
    public void render() {
        long time = System.currentTimeMillis();
        furTime += Gdx.graphics.getDeltaTime();

        if (loadingAssets && assetManager.update()) {
            if (time - startedTime >= splashTimeout) {
                finishedLoading();
            }
        }

        if (loadingAssets) {
            Gdx.gl20.glClearColor(255, 255, 255, 1);
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            if (assetManager.isLoaded(CREATIVE_SPHERE_PNG)) {
                if (creativeSphereSprite == null) {
                    creativeSphereSprite = new Sprite(assetManager.get(CREATIVE_SPHERE_PNG, Texture.class));
                    creativeSphereSprite.flip(false, true);
                    creativeSphereSprite.setCenter(1280 / 2, 800 / 2);
                }

                spriteBatch.begin();
                renderContext.begin();
                creativeSphereSprite.draw(spriteBatch);
                renderContext.end();
                spriteBatch.end();
            }
        } else {
            cameraController.update();
            elapsedTime++;

            forceDirection.x = (float)Math.sin(furTime * 0.2f) * 0.02f;
            forceDirection.y = (float)Math.cos(furTime * 1f) * 0.02f;

            Gdx.gl20.glClearColor(0, 0, 0, 1);
//            // Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
//            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//            Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
//            Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
//            Gdx.gl20.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
//            Gdx.gl20.glPolygonOffset(1.0f, 1.0f);

//            Gdx.gl20.glEnable(GL20.GL_BLEND);
//            Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//            Gdx.gl20.glBlendEquation(GL20.GL_BLEND);


            try {
//                spriteBatch.begin();
//                creativeSphereSprite.draw(spriteBatch);
//                spriteBatch.end();

//                renderContext.begin();
//                shader.begin(perspectiveCamera, renderContext);
//                shader.program.setUniformMatrix("u_projViewTrans", perspectiveCamera.combined);
//                shader.program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
//                shader.program.setUniformf("u_time", elapsedTime);
//                shader.render(renderable);
//                shader.end();

//                modelBatch.begin(perspectiveCamera);
//                modelBatch.render(renderable);
//                modelBatch.end();

                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

                Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
                furTexture.bind();

                Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE1);
                furExtraTexture.bind();

                renderContext.begin();
                furShader.begin(perspectiveCamera, renderContext);

                furShader.program.setUniformMatrix("u_projViewTrans", perspectiveCamera.combined);

                Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
                Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

                furShader.program.setUniformi("u_grass", 0);
                furShader.program.setUniformi("u_extra", 1);
                // shader.program.setUniformf("u_ambientColor", new Color(0.9f, 0.9f, 0.9f, 0.3f));
                displacement.set(gravity).add(forceDirection);
//                displacement.set(0f, 0f, 0f);

                furShader.program.setUniformf("u_displacement", displacement.x, displacement.y, 0.0f);
                furShader.program.setUniformf("u_time", furTime);
                furShader.program.setUniformf("u_hair_length", -0.04f);

//                for (int i = 0; i < LAYERS; i++) {
//                    furShader.program.setUniformf("u_layer", (float)i / (float)LAYERS);
//                    // shader.program.setUniformf("u_layer1", (float)i / (float)LAYERS);
//                    furShader.render(renderable);
//                }
                tdFont.drawTextLine(linearCallback, textPosition, perspectiveCamera, "Hello World");

                textPosition.idt().translate(-5f, 0f, 0f);

                furShader.end();
                renderContext.end();

                directionalLight.setDirection((float)Math.sin(elapsedTime / 50f), -0.8f, (float)Math.cos(elapsedTime / 50f));

                spriteBatch.begin();
                String fpsString = Gdx.graphics.getFramesPerSecond() + "fps";
                float fpsWidth = new GlyphLayout(smallFont, fpsString).width;

//                smallFont.draw(spriteBatch, fpsString, width - fpsWidth, 2);
                smallFont.draw(spriteBatch, fpsString, 100 - fpsWidth, 2);

                if (pointer != null) {
                    pointer.draw(spriteBatch);
                }

                spriteBatch.end();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        lastRendered = time;
    }

    @Override
    public void resize (int width, int height) {
        this.width = width;
        this.height = height;
        orthographicCamera.viewportWidth = width;
        orthographicCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
        perspectiveCamera.viewportHeight = height;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int origScreenX, int origScreenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int origScreenX, int origScreenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int origScreenX, int origScreenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int origScreenX, int origScreenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private static Mesh createRect(float x, float y, float width, float height) {
        Mesh mesh = new Mesh(true, 6, 6,
                VertexAttribute.Position(),
                VertexAttribute.ColorUnpacked(),
                VertexAttribute.TexCoords(0)
            );

        mesh.setVertices(new float[] {
                -1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 0, 0,
                 1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 1, 0,
                 1 * width + x, -1,  1 * height + y, 1, 0, 1, 1, 1, 1,
                -1 * width + x, -1,  1 * height + y, 1, 0, 1, 1, 0, 1
           });

        mesh.setIndices(new short[] {
                0, 1, 2, 3, 4, 5
        });
        return mesh;
    }

    private static Texture fillFurTexture(int width, int height, float density) {
        int totalPixels = width * height;

        Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);

        Random rand = new Random();

        pixmap.setColor(Color.CLEAR);
        pixmap.fill();

        // compute the number of opaque pixels = nr of hair strands
        int nrStrands = (int)(density * totalPixels);
        int strandsPerLayer = nrStrands / LAYERS;

        // fill texture with opaque pixels
        for (int i = 0; i < nrStrands; i++) {
            int x = rand.nextInt(height);
            int y = rand.nextInt(width);
            int max_layer = i / strandsPerLayer;
            //normalize into [0..1] range
            float max_layer_n = max_layer / (float)LAYERS;

            // put color (which has an alpha value of 255, i.e. opaque)
            pixmap.drawPixel(x, y, Color.rgba8888(max_layer_n, 0f, 0f, 1f));
        }
        pixmap.drawPixel(0, 0, Color.rgba8888(0f, 0f, 0f, 0f));
        pixmap.drawPixel(0, 1, Color.rgba8888(0f, 0f, 0f, 0f));
        pixmap.drawPixel(1, 0, Color.rgba8888(0f, 0f, 0f, 0f));
        pixmap.drawPixel(1, 1, Color.rgba8888(0f, 0f, 0f, 0f));

        Texture texture = new Texture(pixmap);
        // pixmap.dispose();
        return texture;
    }
}
