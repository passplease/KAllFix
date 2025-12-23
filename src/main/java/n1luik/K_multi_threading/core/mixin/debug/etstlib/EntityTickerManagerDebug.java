package n1luik.K_multi_threading.core.mixin.debug.etstlib;

import com.c2h6s.etstlib.content.misc.entityTicker.EntityTickerManager;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Slf4j
@Mixin(value = EntityTickerManager.class, remap = false)
public class EntityTickerManagerDebug {
    @Inject(method = "tick", at = @At("RETURN"), remap = false)
    private static void tick(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            log.info("EntityTickerManagerDebug false");
        }
    }
}
