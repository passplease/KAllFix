package n1luik.KAllFix.mixin;

import n1luik.KAllFix.forge.ModInit;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Util.class)
public class UtilMixin {
    @Inject(method = "startTimerHackThread", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        ModInit.initDataCollectors();
    }
}
