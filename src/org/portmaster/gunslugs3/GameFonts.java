package org.portmaster.gunslugs3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.orangepixel.utils.ArcadeCanvas;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Recreate the verified game's UI fonts at a more readable handheld size. */
final class GameFonts {
    private BitmapFont[] adjusted;
    private BitmapFont gameplayFont, menuFont;
    private boolean failed;

    void prepareFrame() {
        if (adjusted == null || gameplayFont == null || menuFont == null) return;
        int state = ArcadeCanvas.GameState;
        adjusted[0] = state == 5 || state == 32 || (state >= 10 && state <= 16)
                ? menuFont : gameplayFont;
    }

    void enlargeWhenReady() {
        if (failed) return;
        try {
            Class<?> gui = Class.forName("com.orangepixel.utils.GUI");
            BitmapFont[] fonts = (BitmapFont[]) gui.getField("fonts").get(null);
            if (fonts == null || fonts == adjusted || fonts.length < 5) return;
            for (int i = 0; i < 4; i++) if (fonts[i] == null) return;

            Field count = gui.getDeclaredField("fontLoadedCount");
            count.setAccessible(true);
            int oldCount = count.getInt(null);
            if (oldCount != 3) return;
            Method load = gui.getMethod("loadFont", String.class, Color.class, int.class,
                    boolean.class, boolean.class);
            BitmapFont[] old = {fonts[0], fonts[1], fonts[2], fonts[3]};
            BitmapFont menu = null;
            count.setInt(null, -1);
            try {
                load.invoke(null, "piXelize.ttf", Color.WHITE, 18, false, true);
                load.invoke(null, "piXelize.ttf", Color.WHITE, 24, true, true);
                load.invoke(null, "piXelize.ttf", Color.BLACK, 18, false, false);
                load.invoke(null, "piXelize.ttf", new Color(1f, .9f, .8f, 1f), 18, false, false);
                load.invoke(null, "piXelize.ttf", Color.WHITE, 16, false, true);
                BitmapFont rawMenu = fonts[4];
                MenuFont compactMenu = new MenuFont(rawMenu);
                boolean menuOwnsTexture = rawMenu.ownsTexture();
                rawMenu.setOwnsTexture(false);
                compactMenu.setOwnsTexture(menuOwnsTexture);
                menu = compactMenu;
                fonts[4] = null;
                rawMenu.dispose();
                count.setInt(null, oldCount);
                BitmapFont small = fonts[0];
                HudFont hud = new HudFont(small, gui.getField("rightAlign"),
                        Class.forName("com.orangepixel.utils.Render").getField("width"));
                boolean ownsTexture = small.ownsTexture();
                small.setOwnsTexture(false);
                hud.setOwnsTexture(ownsTexture);
                fonts[0] = hud;
                small.dispose();
                menuFont = menu;
                gameplayFont = hud;
            } catch (Exception e) {
                if (menu != null) menu.dispose();
                if (fonts.length > 4 && fonts[4] != null) {
                    fonts[4].dispose();
                    fonts[4] = null;
                }
                for (int i = 0; i < old.length; i++) {
                    if (fonts[i] != null && fonts[i] != old[i]) fonts[i].dispose();
                    fonts[i] = old[i];
                }
                count.setInt(null, oldCount);
                throw e;
            }
            for (BitmapFont font : old) font.dispose();
            adjusted = fonts;
            System.out.println("GAME_FONTS_OK 10->18 (menus 16), 20->24");
        } catch (Exception e) {
            failed = true;
            System.err.println("GAME_FONTS_FAILED: " + e);
        }
    }

    /** Keep the long video option inside the original menu panel. */
    private static final class MenuFont extends BitmapFont {
        MenuFont(BitmapFont font) {
            super(font.getData(), font.getRegions(), font.usesIntegerPositions());
        }

        @Override public void draw(Batch batch, GlyphLayout layout, float x, float y) {
            int glyphCount = 0;
            for (GlyphLayout.GlyphRun run : layout.runs) glyphCount += run.glyphs.size;
            if (glyphCount >= 24) {
                StringBuilder text = new StringBuilder(glyphCount);
                for (GlyphLayout.GlyphRun run : layout.runs)
                    for (BitmapFont.Glyph glyph : run.glyphs) text.append((char) glyph.id);
                if ("Tips&Tricks video (youtube)".equalsIgnoreCase(text.toString()))
                    layout.setText(this, "TIPS VIDEO (YOUTUBE)");
            }
            super.draw(batch, layout, x, y);
        }
    }

    /** The game's right-aligned XP counter shares the bar's Y coordinate. */
    private static final class HudFont extends BitmapFont {
        private final Field rightAlign, renderWidth;
        private boolean reported;

        HudFont(BitmapFont font, Field rightAlign, Field renderWidth) {
            super(font.getData(), font.getRegions(), font.usesIntegerPositions());
            this.rightAlign = rightAlign;
            this.renderWidth = renderWidth;
        }

        @Override public void draw(Batch batch, GlyphLayout layout, float x, float y) {
            try {
                if (ArcadeCanvas.GameState == 7 && rightAlign.getBoolean(null)
                        && x > renderWidth.getInt(null) / 2f && y < 20 && isCounter(layout)) {
                    y += 6;
                    if (!reported) {
                        reported = true;
                        System.out.println("HUD_XP_LABEL_BELOW_BAR");
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot position HUD counter", e);
            }
            super.draw(batch, layout, x, y);
        }

        private boolean isCounter(GlyphLayout layout) {
            boolean slash = false, before = false, after = false;
            for (GlyphLayout.GlyphRun run : layout.runs) {
                for (BitmapFont.Glyph glyph : run.glyphs) {
                    if (glyph.id == '/' && !slash && before) slash = true;
                    else if (glyph.id >= '0' && glyph.id <= '9') {
                        if (slash) after = true; else before = true;
                    } else return false;
                }
            }
            return slash && after;
        }
    }
}
