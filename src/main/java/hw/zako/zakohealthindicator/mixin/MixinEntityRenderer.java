package hw.zako.zakohealthindicator.mixin;

import hw.zako.zakohealthindicator.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Unique private boolean chi$handling = false;

    @Shadow
    protected abstract void renderLabelIfPresent(EntityRenderState state, Text text,
            MatrixStack ms, VertexConsumerProvider vcp, int light);

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void chi$label(EntityRenderState state, Text text,
                           MatrixStack ms, VertexConsumerProvider vcp, int light,
                           CallbackInfo ci) {
        if (!(state instanceof PlayerEntityRenderState playerState)) return;

        String playerName = playerState.name;
        String textStr    = text.getString();

        if (playerName != null && !playerName.isEmpty() && !textStr.contains(playerName)) {
            ci.cancel();
            return;
        }

        if (chi$handling) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        float hp = 0f, max = 20f;
        if (mc.world != null) {
            Entity entity = mc.world.getEntityById(playerState.id);
            if (entity instanceof LivingEntity living) {
                hp  = living.getHealth();
                max = living.getMaxHealth();
            }
        }

        int   rgb        = ColorUtil.hpColor(max > 0f ? hp / max : 1f);
        Style numStyle   = Style.EMPTY.withColor(TextColor.fromRgb(rgb));
        Style heartStyle = Style.EMPTY.withColor(TextColor.fromRgb(rgb))
                .withFont(Identifier.of("customhealthindicator", "icons"));

        MutableText modified = text.copy()
                .append(Text.literal(" " + String.format("%.1f", hp)).setStyle(numStyle))
                .append(Text.literal("\uE001").setStyle(heartStyle));

        chi$handling = true;
        renderLabelIfPresent(state, modified, ms, vcp, light);
        chi$handling = false;
        ci.cancel();
    }
}
