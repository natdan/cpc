package org.ah.cpc;

import static org.ah.cpc.CPCConstants.CREATIVE_SPHERE_PNG;
import static org.ah.cpc.CPCConstants.CURSOR_EMPTY_PNG;
import static org.ah.cpc.CPCConstants.CURSOR_PNG;
import static org.ah.cpc.CPCConstants.FONT_32_FNT;
import static org.ah.cpc.PlatformSpecific.CONFIG_IS_DESKTOP;
import static org.ah.cpc.PlatformSpecific.CONFIG_MOUSE_SHOW;
import static org.ah.cpc.PlatformSpecific.CONFIG_UI_INPUT_DEBUG;

import org.ah.libgdx.components.Component;
import org.ah.libgdx.components.util.RenderContext;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CPCounter extends ApplicationAdapter implements InputProcessor {

    public static final float CLICK_FEEDBACK_TIMEOUT = 1250f;
    // private LibGDXLogger logger = new LibGDXLogger("Frontend");

    private AssetManager assetManager;
    // private AssetTextureProvider textureProvider;
    private boolean loadingAssets = true;

    private PlatformSpecific platformSpecific;

    private Sprite creativeSphereSprite;

    private RenderContext renderContext;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private BitmapFont smallFont;

    private long startedTime;
    private long lastRendered;

    private long splashTimeout = 1000;

    private Sprite pointer;

    private int width;
    private int height;

    public CPCounter(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;

        Component.uiInputDebug = platformSpecific.getConfigParameterAsBool(CONFIG_UI_INPUT_DEBUG);
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

        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(true, 1280, 800);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);

        renderContext = new RenderContext();
        renderContext.setSpriteBatch(batch);
        renderContext.setShapeRenderer(shapeRenderer);

        assetManager.load(CREATIVE_SPHERE_PNG, Texture.class);
        assetManager.load(CURSOR_PNG, Pixmap.class);
        assetManager.load(CURSOR_EMPTY_PNG, Pixmap.class);

        BitmapFontParameter bitmapFontParameter = new BitmapFontParameter();
        bitmapFontParameter.flip = true;

        assetManager.load(FONT_32_FNT, BitmapFont.class, bitmapFontParameter);

        Gdx.input.setInputProcessor(this);

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

        smallFont = assetManager.get(FONT_32_FNT);
    }

    @Override
    public void render() {
        long time = System.currentTimeMillis();

//        if (pointer != null && platformSpecific.getConfigParameterAsBool(CONFIG_MOUSE_DEBUG)) {
//            updatePointer(time);
//        }

        if (loadingAssets && assetManager.update()) {
            if (time - startedTime >= splashTimeout) {
                finishedLoading();
            }
        }

        if (loadingAssets) {
            Gdx.gl.glClearColor(255, 255, 255, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            if (assetManager.isLoaded(CREATIVE_SPHERE_PNG)) {
                if (creativeSphereSprite == null) {
                    creativeSphereSprite = new Sprite(assetManager.get(CREATIVE_SPHERE_PNG, Texture.class));
                    creativeSphereSprite.flip(false, true);
                    creativeSphereSprite.setCenter(1280 / 2, 800 / 2);
                }

                renderContext.begin();
                creativeSphereSprite.draw(renderContext.getSpriteBatch());
                renderContext.end();
            }
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderContext.begin();
            if (!loadingAssets) {
                try {
                    smallFont.draw(batch, "A flat string 00:00:00:00", 100, 100);
                    // TODO Main rendering and animation goes here
                } catch (Exception e) {
                }
            }

            if (pointer != null) {
                pointer.draw(batch);
            }

            renderContext.end();
        }

        lastRendered = time;
    }

    @Override
    public void resize (int width, int height) {
        this.width = width;
        this.height = height;
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        // camera.setToOrtho(true, width, height);
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
}
