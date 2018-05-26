package org.ah.cpc;

import static org.ah.cpc.CPCConstants.CREATIVE_SPHERE_PNG;
import static org.ah.cpc.CPCConstants.CURSOR_EMPTY_PNG;
import static org.ah.cpc.CPCConstants.CURSOR_PNG;
import static org.ah.cpc.CPCConstants.FONT_32_FNT;
import static org.ah.cpc.PlatformSpecific.CONFIG_IS_DESKTOP;
import static org.ah.cpc.PlatformSpecific.CONFIG_MOUSE_SHOW;

import org.ah.libgdx.font.TDFont;
import org.ah.libgdx.font.TDFont.LinearXYCallback;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
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

    private long splashTimeout = 100;

    private Sprite pointer;

    private Mesh backgroundMesh;
    // private ShaderProgram backgroundShaderProgram;
    private Renderable renderable;
    private DefaultShader shader;

    private int width;
    private int height;

    private TDFont tdFont;
    private Matrix4 textPosition = new Matrix4();
    private LinearXYCallback linearCallback;
    private InputMultiplexer cameraInputMultiplexer;
    private CameraInputController camController;
    private Environment environment;


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
        perspectiveCamera.position.set(0f, 2f, 10f);
        perspectiveCamera.up.set(new Vector3(0, 1, 0));
        perspectiveCamera.lookAt(0f, 0f, 0f);

        perspectiveCamera.near = 0.02f;
        perspectiveCamera.far = 1000f;
        perspectiveCamera.update();

        camController = new CameraInputController(perspectiveCamera);
        camController.target.set(new Vector3(0, 0, 0));
        cameraInputMultiplexer = new InputMultiplexer();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(camController);
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
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        assetManager.load(CREATIVE_SPHERE_PNG, Texture.class);
        assetManager.load(CURSOR_PNG, Pixmap.class);
        assetManager.load(CURSOR_EMPTY_PNG, Pixmap.class);

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

        tdFont = new TDFont();
        tdFont.loadFont("arial-3d.font");

        linearCallback = new TDFont.LinearXYCallback(null, 0.015f) {
            @Override public void begin(Camera cam, Matrix4 model) {
                super.begin(cam, model);
            }
        };

        smallFont = assetManager.get(FONT_32_FNT);

        backgroundMesh = createRect(0, 0, 0.5f, 0.5f);
        renderable = new Renderable();

        renderable.meshPart.mesh = backgroundMesh;
        renderable.meshPart.size = backgroundMesh.getNumIndices();
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;

        renderable.material = null;
        renderable.worldTransform.idt();

        String vertexProgram = Gdx.files.internal("shaders/background.vs").readString();
        String fragmentProgram = Gdx.files.internal("shaders/background.fs").readString();

        shader = new DefaultShader(renderable, new DefaultShader.Config(vertexProgram, fragmentProgram));
        shader.init();
        if (!shader.program.isCompiled()) {
            Gdx.app.log("Shader error: ", shader.program.getLog());
            System.out.println("Shader error" + shader.program.getLog());
            System.exit(-1);
        }

    }

    @Override
    public void render() {
        long time = System.currentTimeMillis();

        if (loadingAssets && assetManager.update()) {
            if (time - startedTime >= splashTimeout) {
                finishedLoading();
            }
        }

        if (loadingAssets) {
            Gdx.gl20.glClearColor(255, 255, 255, 1);
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
            camController.update();
            elapsedTime++;

            Gdx.gl20.glClearColor(0, 0, 0, 1);
            // Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
            Gdx.gl20.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
            Gdx.gl20.glPolygonOffset(1.0f, 1.0f);

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

                textPosition.idt().translate(-5f, 0f, 0f);
                tdFont.drawTextLine(linearCallback, textPosition, perspectiveCamera, "Hello World");

                spriteBatch.begin();
                String fpsString = Gdx.graphics.getFramesPerSecond() + "fps";
                float fpsWidth = new GlyphLayout(smallFont, fpsString).width;

                smallFont.draw(spriteBatch, fpsString, width - fpsWidth - 2, 2);

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
}
