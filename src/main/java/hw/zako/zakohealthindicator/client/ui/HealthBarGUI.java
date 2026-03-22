package hw.zako.zakohealthindicator.client.ui;

import hw.zako.zakohealthindicator.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import org.joml.Matrix3x2fStack;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class HealthBarGUI {

    private static final Random RNG = new Random();

    private final AtomicReference<LivingEntity> pendingTarget = new AtomicReference<>(null);
    private volatile float pendingHp         = 0f;
    private volatile long  pendingAttackTime = -1L;

    private LivingEntity target      = null;
    private float        hpBeforeHit = 0f;
    private long         attackTime  = -1L;
    private boolean      spawned     = false;

    private String flyText  = "";
    private float  flyX, flyY, flyVx, flyVy;
    private long   flyBirth = -1L;

    public void onAttack(LivingEntity entity) {
        pendingHp         = entity.getHealth();
        pendingAttackTime = System.currentTimeMillis();
        pendingTarget.set(entity);
    }

    public void render(DrawContext context) {
        long now = System.currentTimeMillis();

        LivingEntity pending = pendingTarget.getAndSet(null);
        if (pending != null) {
            target      = pending;
            hpBeforeHit = pendingHp;
            attackTime  = pendingAttackTime;
            spawned     = false;
            flyBirth    = -1L;
        }

        if (target == null) return;

        MinecraftClient  mc   = MinecraftClient.getInstance();
        TextRenderer     font = mc.textRenderer;
        Matrix3x2fStack  ms   = context.getMatrices();
        int cx = mc.getWindow().getScaledWidth()  / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;

        if (!spawned) {
            long since = now - attackTime;
            if (since >= 50L) {
                float dmg = hpBeforeHit - target.getHealth();
                if (dmg > 0f) {
                    flyText = String.format("%.1f", dmg);
                    double angle = RNG.nextDouble() * 2 * Math.PI;
                    float  speed = 100f + RNG.nextFloat() * 80f;
                    flyX    = (RNG.nextFloat() - 0.5f) * 10f;
                    flyY    = (RNG.nextFloat() - 0.5f) * 10f;
                    flyVx   = (float) Math.cos(angle) * speed;
                    flyVy   = (float) Math.sin(angle) * speed;
                    flyBirth = now;
                    spawned  = true;
                } else if (since >= 600L) {
                    spawned = true;
                }
            }
        }

        // Big HP number below crosshair
        {
            long el = now - attackTime;
            if (el >= 0L && el <= 2000L) {
                float hp = target.getHealth();
                if (hp > 0f) {
                    String txt   = String.valueOf((int) Math.ceil(hp));
                    float  alpha = el > 1500L ? 1f - (el - 1500f) / 500f : 1f;
                    int    a     = (int)(alpha * 255) & 0xFF;
                    int    color = (a << 24) | 0xFF3030;
                    float  scale = 4f * Config.get().getBigScale();
                    int    tw    = font.getWidth(txt);

                    ms.pushMatrix();
                    ms.translate(cx + Config.get().getBigOffsetX(), cy + 35f + Config.get().getBigOffsetY());
                    ms.scale(scale, scale);
                    context.drawTextWithShadow(font, txt, -tw / 2, 0, color);
                    ms.popMatrix();
                }
            }
        }

        // Flying damage number
        if (flyBirth >= 0L) {
            long age = now - flyBirth;
            if (age <= 1500L) {
                float t     = age / 1000f;
                float alpha = age > 1000L ? 1f - (age - 1000f) / 500f : 1f;
                int   a     = (int)(alpha * 255) & 0xFF;
                int   color = (a << 24) | 0xFF3030;

                float fx    = flyX + flyVx * t;
                float fy    = flyY + flyVy * t;
                float scale = Config.get().getFlyingScale();
                int   tw    = font.getWidth(flyText);

                ms.pushMatrix();
                ms.translate(cx + fx, cy + fy);
                ms.scale(scale, scale);
                context.drawTextWithShadow(font, flyText, -tw / 2, 0, color);
                ms.popMatrix();
            } else {
                flyBirth = -1L;
            }
        }
    }
}
