package n1luik.K_multi_threading.core.mixin.debug;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(Entity.class)
public class EntityDebug {
    @Deprecated
    @Inject(method = "setId", at = @At("RETURN"))
    private void debug1(int p_132212_, CallbackInfo ci){
        Base.LOGGER.info("Entity id {}", p_132212_, new Throwable());
    }
}
