package n1luik.K_multi_threading.core.mixin.debug;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Mixin(value = PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerDebug {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "addEntityWithoutEvent", at = @At("HEAD"), remap = false)
    public <T extends EntityAccess> void debug1(T p_157539_, boolean p_157540_, CallbackInfoReturnable<Boolean> cir){
        LOGGER.info("addEntityWithoutEvent", new Throwable());
    }
}
