package hw.zako.zakohealthindicator.mixin;

import hw.zako.zakohealthindicator.client.KeyBindings;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(GameOptions.class)
public class MixinGameOptions {

    @Shadow @Final @Mutable
    public KeyBinding[] allKeys;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void chi$injectKey(CallbackInfo ci) {
        for (KeyBinding kb : allKeys) {
            if (kb == KeyBindings.OPEN_SETTINGS) return;
        }
        KeyBinding[] extended = Arrays.copyOf(allKeys, allKeys.length + 1);
        extended[allKeys.length] = KeyBindings.OPEN_SETTINGS;
        allKeys = extended;
    }
}
