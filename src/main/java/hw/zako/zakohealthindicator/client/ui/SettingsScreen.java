package hw.zako.zakohealthindicator.client.ui;

import hw.zako.zakohealthindicator.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class SettingsScreen extends Screen {

    private static final float SCALE_MIN = 0.25f;
    private static final float SCALE_MAX = 5.0f;
    private static final float OX_MIN    = -200f;
    private static final float OX_MAX    =  200f;
    private static final float OY_MIN    = -100f;
    private static final float OY_MAX    =  100f;
    private static final int   W         = 220;
    private static final int   H         = 20;

    private final Screen parent;

    public SettingsScreen(Screen parent) {
        super(Text.literal("Health Indicator Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width  / 2;
        int cy = height / 2;

        addDrawableChild(makeScaleSlider(cx - W / 2, cy - 65, "Flying number size",
                Config.get().getFlyingScale(),
                v -> Config.get().setFlyingScale(v)));

        addDrawableChild(makeScaleSlider(cx - W / 2, cy - 35, "Low HP number size",
                Config.get().getBigScale(),
                v -> Config.get().setBigScale(v)));

        addDrawableChild(makeOffsetSlider(cx - W / 2, cy - 5, "Low HP position: Up/Down",
                OY_MIN, OY_MAX, Config.get().getBigOffsetY(),
                v -> Config.get().setBigOffsetY(v)));

        addDrawableChild(makeOffsetSlider(cx - W / 2, cy + 25, "Low HP position: Left/Right",
                OX_MIN, OX_MAX, Config.get().getBigOffsetX(),
                v -> Config.get().setBigOffsetX(v)));

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> close())
                .dimensions(cx - 50, cy + 60, 100, H)
                .build());
    }

    private SliderWidget makeScaleSlider(int x, int y, String label,
                                         float init, java.util.function.Consumer<Float> onChange) {
        double norm = (init - SCALE_MIN) / (SCALE_MAX - SCALE_MIN);
        return new SliderWidget(x, y, W, H, Text.empty(), norm) {
            @Override protected void updateMessage() {
                setMessage(Text.literal(label + ": " + String.format("%.2f", val()) + "x"));
            }
            @Override protected void applyValue() { onChange.accept(val()); }
            private float val() {
                return Math.round((SCALE_MIN + (float)(value * (SCALE_MAX - SCALE_MIN))) * 100f) / 100f;
            }
        };
    }

    private SliderWidget makeOffsetSlider(int x, int y, String label,
                                          float min, float max, float init,
                                          java.util.function.Consumer<Float> onChange) {
        double norm = (init - min) / (max - min);
        return new SliderWidget(x, y, W, H, Text.empty(), norm) {
            @Override protected void updateMessage() {
                int v = val();
                setMessage(Text.literal(label + ": " + (v >= 0 ? "+" : "") + v + "px"));
            }
            @Override protected void applyValue() { onChange.accept((float) val()); }
            private int val() { return Math.round(min + (float)(value * (max - min))); }
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 85, 0xFFFFFF);
    }

    @Override
    public void close() { client.setScreen(parent); }

    @Override
    public boolean shouldPause() { return false; }
}
