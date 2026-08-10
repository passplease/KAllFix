package n1luik.K_multi_threading.neoforge.mixin.debug.ae;

import appeng.blockentity.spatial.SpatialPylonBlockEntity;
import lombok.extern.slf4j.Slf4j;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Slf4j
@Deprecated
@Mixin(value = SpatialPylonBlockEntity.class)
public class SpatialPylonBlockEntityMixin {

    @Inject(method = "recalculateDisplay", at = @At("HEAD"))
    public void recalculateDisplay(CallbackInfo ci) {
        log.info("recalculateDisplay", new Throwable());
    }
}