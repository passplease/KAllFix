package n1luik.K_multi_threading.core.mixin.debug.citadel;

import com.github.alexthe666.citadel.ServerProxy;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Slf4j
@Mixin(value = ServerProxy.class, remap = false)
public class ServerProxyDebug {
    @Inject(method = "canEntityTickServer", at = @At("RETURN"), remap = false)
    private void debug1(Level level, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            log.info("ServerProxyDebug false");
        }
    }
}
