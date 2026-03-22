package hw.zako.zakohealthindicator.mixin;

import hw.zako.zakohealthindicator.client.KeyBindings;
import hw.zako.zakohealthindicator.client.ui.SettingsScreen;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Unique private boolean        chi$setupDone    = false;
    @Unique private boolean        chi$setupFailed  = false;
    @Unique private FontManager    chi$fontManager  = null;
    @Unique private Field          chi$storagesField = null;
    @Unique private TextureManager chi$textureManager = null;
    @Unique private Constructor<?> chi$glyphCtor    = null;
    @Unique private int            chi$imgW, chi$imgH;
    @Unique private float          chi$scaleFactor;
    @Unique private int            chi$ascent;
    @Unique private NativeImage    chi$heartImg     = null;

    @Inject(method = "tick", at = @At("TAIL"))
    private void chi$tick(CallbackInfo ci) {
        MinecraftClient mc = (MinecraftClient)(Object)this;
        if (!chi$setupDone && !chi$setupFailed) chi$setup(mc);
        if (!chi$setupFailed) chi$ensureFont();
        if (mc.currentScreen == null && KeyBindings.OPEN_SETTINGS.wasPressed()) {
            mc.setScreen(new SettingsScreen(null));
        }
    }

    @Unique
    private void chi$setup(MinecraftClient mc) {
        try {
            for (Field f : MinecraftClient.class.getDeclaredFields()) {
                if (f.getType() == FontManager.class) {
                    f.setAccessible(true);
                    chi$fontManager = (FontManager) f.get(mc);
                    break;
                }
            }
            if (chi$fontManager == null) { chi$setupFailed = true; return; }

            for (Field f : FontManager.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    chi$storagesField = f;
                    break;
                }
            }
            if (chi$storagesField == null) { chi$setupFailed = true; return; }

            for (Field f : FontManager.class.getDeclaredFields()) {
                if (f.getType() == TextureManager.class) {
                    f.setAccessible(true);
                    chi$textureManager = (TextureManager) f.get(chi$fontManager);
                    break;
                }
            }
            if (chi$textureManager == null) { chi$setupFailed = true; return; }

            InputStream is = MixinMinecraftClient.class.getClassLoader()
                    .getResourceAsStream("assets/customhealthindicator/font/heart.png");
            if (is == null) { chi$setupFailed = true; return; }
            chi$heartImg = NativeImage.read(is);
            is.close();

            chi$imgW = chi$heartImg.getWidth();
            chi$imgH = chi$heartImg.getHeight();
            chi$scaleFactor = 9.0f / chi$imgH;
            chi$ascent = Math.round(7.0f / chi$scaleFactor);

            for (Class<?> inner : BitmapFont.class.getDeclaredClasses()) {
                if (inner.isRecord()) {
                    chi$glyphCtor = inner.getDeclaredConstructors()[0];
                    chi$glyphCtor.setAccessible(true);
                    break;
                }
            }
            if (chi$glyphCtor == null) { chi$setupFailed = true; return; }

            chi$setupDone = true;
        } catch (Exception e) {
            System.err.println("[CHI] Setup failed: " + e);
            chi$setupFailed = true;
        }
    }

    @Unique
    private void chi$ensureFont() {
        try {
            @SuppressWarnings("unchecked")
            Map<Identifier, FontStorage> storages =
                    (Map<Identifier, FontStorage>) chi$storagesField.get(chi$fontManager);

            Identifier iconId = Identifier.of("customhealthindicator", "icons");
            if (storages.containsKey(iconId)) return;

            final Glyph heartGlyph = (Glyph) chi$glyphCtor.newInstance(
                    chi$scaleFactor, chi$heartImg, 0, 0, chi$imgW, chi$imgH, chi$imgW, chi$ascent);

            Font heartFont = new Font() {
                @Override public Glyph getGlyph(int cp) { return cp == 0xE001 ? heartGlyph : null; }
                @Override public IntSet getProvidedGlyphs() {
                    IntOpenHashSet s = new IntOpenHashSet(); s.add(0xE001); return s;
                }
            };

            Font.FontFilterPair pair = new Font.FontFilterPair(heartFont, FontFilterType.FilterMap.NO_FILTER);
            FontStorage storage = new FontStorage(chi$textureManager, iconId);
            storage.setFonts(Collections.singletonList(pair), Collections.emptySet());
            storages.put(iconId, storage);

        } catch (Exception ignored) {}
    }
}
