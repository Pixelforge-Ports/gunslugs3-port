package org.portmaster.gunslugs3;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.orangepixel.gunslugs3.myCanvas;
import com.orangepixel.plugins.Social;
import java.lang.reflect.*;
import java.nio.file.*;

/** Native desktop libGDX host. All original game code stays in the owner's jar. */
public class Main extends myCanvas {
    private static final int LEGACY_ESCAPE_KEY = 131;
    public final DisplayLayout layout = new DisplayLayout();
    protected Graphics physicalGraphics;
    private GL20 physicalGl;
    private GL20 bridgeGl;
    private Graphics bridgeGraphics;
    private Application physicalApp;
    private boolean ready;
    private long lastFrame;
    private int frames;
    private long start;
    private final GameFonts gameFonts = new GameFonts();

    public static Lwjgl3ApplicationConfiguration configuration() throws Exception {
        int width = Integer.getInteger("gunslugs3.width",640), height = Integer.getInteger("gunslugs3.height",480);
        if (width < 160 || height < 160 || width > 8192 || height > 8192)
            throw new IllegalArgumentException("Display dimensions must be 160..8192 pixels");
        Path saves = Paths.get(System.getProperty("gunslugs3.saves","saves")).toAbsolutePath();
        java.nio.file.Files.createDirectories(saves);
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Gunslugs 3 Rogue Tactics"); cfg.setWindowedMode(width,height);
        cfg.setHdpiMode(HdpiMode.Pixels); cfg.setResizable(false);
        cfg.setForegroundFPS(60); cfg.setIdleFPS(30); cfg.useVsync(false);
        java.nio.file.Files.createDirectories(saves.resolve("legacy-preferences"));
        cfg.setPreferencesConfig(saves.resolve("legacy-preferences").toString(), com.badlogic.gdx.Files.FileType.Absolute);
        cfg.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL20,2,0);
        cfg.disableAudio(Boolean.getBoolean("gunslugs3.noAudio"));
        cfg.setInitialVisible(!Boolean.getBoolean("gunslugs3.hidden"));
        if (Boolean.getBoolean("gunslugs3.fullscreen")) cfg.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        return cfg;
    }

    public static void main(String[] args) throws Exception {
        VerifyGame.check(Paths.get(System.getProperty("gunslugs3.dat","gunslugs3.dat")));
        System.out.println("Gunslugs 3 Rogue Tactics PortMaster host 0.1.0 | " + System.getProperty("os.arch") + " | 60 updates/s");
        new Lwjgl3Application(new Main(),configuration());
    }

    @Override public void create() {
        physicalApp = Gdx.app; physicalGraphics = Gdx.graphics; physicalGl = Gdx.gl20;
        layout.resize(physicalGraphics.getWidth(),physicalGraphics.getHeight());
        installDisplayBridge();
        // Use the game's offline path; never construct the desktop Steam launcher.
        mySocial = (Social)Proxy.newProxyInstance(Main.class.getClassLoader(),new Class<?>[]{Social.class},(self,method,args)-> {
            Class<?> type = method.getReturnType();
            if (type == boolean.class) return false;
            if (type == int.class) return 0;
            if (type == String.class) return "";
            if (type == String[].class) return new String[0];
            return null;
        });
        argument_noController = true;
        super.create();
        Gdx.input.setInputProcessor(inputBridge(Gdx.input.getInputProcessor(),layout));
        ready = true;
        resize(physicalGraphics.getWidth(),physicalGraphics.getHeight());
        start = System.nanoTime();
        System.out.println("GAME_CREATE_OK offline; PortMaster keyboard input; saves=" + System.getProperty("gunslugs3.saves","saves"));
    }

    static InputProcessor inputBridge(InputProcessor input,DisplayLayout layout) {
        return (InputProcessor)Proxy.newProxyInstance(Main.class.getClassLoader(),new Class<?>[]{InputProcessor.class},(self,method,args)-> {
            String name = method.getName();
            if ((name.equals("keyDown") || name.equals("keyUp")) && (Integer)args[0] == Input.Keys.ESCAPE) {
                Object[] legacyArgs = args.clone();
                legacyArgs[0] = LEGACY_ESCAPE_KEY;
                boolean legacyHandled = (Boolean)delegate(input,method,legacyArgs);
                boolean currentHandled = (Boolean)delegate(input,method,args);
                return legacyHandled || currentHandled;
            }
            if (name.startsWith("touch") || name.equals("mouseMoved")) {
                args = args.clone(); args[0] = layout.inputX((Integer)args[0]); args[1] = layout.inputY((Integer)args[1]);
            }
            return delegate(input,method,args);
        });
    }

    @Override public void resize(int width,int height) {
        if (!ready || width < 160 || height < 160) return;
        restoreDisplayBridge();
        layout.resize(width,height);
        super.resize(layout.gameWidth,layout.gameHeight);
        System.out.println("GAME_RESIZE_OK " + width + "x" + height + " view=" + layout.gameWidth + "x" + layout.gameHeight);
    }
    @Override public void render() {
        restoreDisplayBridge();
        // Match this Windows build's original 60 FPS cap.
        long now = System.nanoTime();
        while (lastFrame != 0 && now - lastFrame < 16666667L) {
            java.util.concurrent.locks.LockSupport.parkNanos(16666667L - (now - lastFrame));
            if (Thread.currentThread().isInterrupted()) break;
            now = System.nanoTime();
        }
        lastFrame = now; // no catch-up updates after a stall/resume
        physicalGl.glDisable(GL20.GL_SCISSOR_TEST);
        physicalGl.glClearColor(0,0,0,1); physicalGl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gameFonts.prepareFrame();
        super.render();
        gameFonts.enlargeWhenReady();
        frames++;
        int smoke = Integer.getInteger("gunslugs3.smokeFrames",0);
        if (smoke > 0 && frames >= smoke) {
            String capture = System.getProperty("gunslugs3.capture");
            if (capture != null) capture(capture);
            System.out.println("SMOKE_OK frames=" + frames + " state=" + GameState + " seconds=" + (System.nanoTime()-start)/1e9);
            Gdx.app.exit();
        }
    }
    public void capture(String path) {
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0,0,physicalGraphics.getBackBufferWidth(),physicalGraphics.getBackBufferHeight());
        try { PixmapIO.writePNG(Gdx.files.absolute(path),pixmap,-1,true); }
        finally { pixmap.dispose(); }
    }
    @Override public void pause() { if (activePlayer != null) activePlayer.saveSettings(); super.pause(); }
    @Override public void resume() { lastFrame = 0; super.resume(); }
    @Override public void dispose() {
        if (activePlayer != null) activePlayer.saveSettings();
        try { if (ready) super.dispose(); }
        finally { if (physicalGraphics != null) { Gdx.app = physicalApp; Gdx.graphics = physicalGraphics; Gdx.gl = physicalGl; Gdx.gl20 = physicalGl; } }
    }

    private static Object delegate(Object target,Method method,Object[] args) throws Throwable {
        try { return method.invoke(target,args); }
        catch (InvocationTargetException e) { throw e.getCause(); }
    }
    private void restoreDisplayBridge() {
        if (bridgeGraphics != null) { Gdx.graphics = bridgeGraphics; Gdx.gl = bridgeGl; Gdx.gl20 = bridgeGl; }
    }
    private void installDisplayBridge() {
        final int[] framebuffer = {0};
        GL20 gl = (GL20)Proxy.newProxyInstance(Main.class.getClassLoader(),new Class<?>[]{GL20.class},(self,method,args)-> {
            String name = method.getName();
            if (name.equals("glBindFramebuffer")) framebuffer[0] = (Integer)args[1];
            if (framebuffer[0] == 0 && (name.equals("glViewport") || name.equals("glScissor"))) {
                args = new Object[]{layout.viewportX((Integer)args[0]),layout.viewportY((Integer)args[1]),
                    layout.viewportWidth((Integer)args[2]),layout.viewportHeight((Integer)args[3])};
            }
            return delegate(physicalGl,method,args);
        });
        Gdx.gl = gl; Gdx.gl20 = gl;
        Gdx.graphics = (Graphics)Proxy.newProxyInstance(Main.class.getClassLoader(),new Class<?>[]{Graphics.class},(self,method,args)-> {
            switch (method.getName()) {
                case "getWidth": case "getBackBufferWidth": return layout.gameWidth;
                case "getHeight": case "getBackBufferHeight": return layout.gameHeight;
                case "getGL20": return gl;
                case "setWindowedMode": case "setFullscreenMode": case "supportsDisplayModeChange": return false;
                case "setVSync": return null;
                default: return delegate(physicalGraphics,method,args);
            }
        });
        bridgeGl = gl; bridgeGraphics = Gdx.graphics;
        final Application originalApp = Gdx.app;
        Gdx.app = (Application)Proxy.newProxyInstance(Main.class.getClassLoader(),new Class<?>[]{Application.class},(self,method,args)-> {
            if (method.getName().equals("getGraphics")) return Gdx.graphics;
            return delegate(originalApp,method,args);
        });

    }
}
