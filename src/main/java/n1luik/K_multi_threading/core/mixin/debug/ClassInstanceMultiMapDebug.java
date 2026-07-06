package n1luik.K_multi_threading.core.mixin.debug;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Mixin(value = ClassInstanceMultiMap.class)
public class ClassInstanceMultiMapDebug {

    @Inject(method = "add", at = @At("HEAD"))
    public void debug1(CallbackInfoReturnable<Boolean> cir){
        Base.LOGGER.info("addEntityWithoutEvent", new Throwable());
    }
}
