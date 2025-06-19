package n1luik.K_multi_threading.core.mixin.fix.canary;

import com.abdelaziz.canary.common.config.CanaryConfig;
import com.abdelaziz.canary.common.config.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//无效
@Deprecated
@Mixin(CanaryConfig.class)
public class CanaryConfigMixin {
    @Inject(method = "getEffectiveOptionForMixin", at = @At("HEAD"), cancellable = true, remap = false)
    public void fix1(String mixinClassName, CallbackInfoReturnable<Option> cir) {
        if (mixinClassName.startsWith("collections.entity_by_type.")) {
            cir.setReturnValue(null);
            return;
            }
        if (mixinClassName.startsWith("world.tick_scheduler.")) {
            cir.setReturnValue(null);
            return;
        }
    }
}
