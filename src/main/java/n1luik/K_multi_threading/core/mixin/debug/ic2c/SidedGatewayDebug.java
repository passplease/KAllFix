package n1luik.K_multi_threading.core.mixin.debug.ic2c;

import ic2.core.utils.SidedGateway;
import n1luik.K_multi_threading.core.Base;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Deprecated
@Mixin(SidedGateway.class)
public class SidedGatewayDebug<T> {
    @Shadow(remap = false) @Final private T serverInstance;

    @Shadow(remap = false) @Final private T clientInstance;

    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"), remap = false)
    public void debug1(String server, String client, CallbackInfo ci) {
        Base.LOGGER.info("SidedGateway: " + server + " " + client, new Exception());
    }

    @Inject(method = "<init>(Ljava/lang/Class;Ljava/lang/Class;)V", at = @At("RETURN"), remap = false)
    public void debug2(Class server, Class client, CallbackInfo ci) {
        Base.LOGGER.info("SidedGateway: " + server + " " + client, new Exception());
    }

    @Inject(method = "<init>(Ljava/util/function/Supplier;Ljava/util/function/Supplier;)V", at = @At("RETURN"), remap = false)
    public void debug3(Supplier server, Supplier client, CallbackInfo ci) {
        Base.LOGGER.info("SidedGateway: " + (serverInstance == null ? "null" : serverInstance.getClass()) + " " + (clientInstance == null ? "null" : clientInstance.getClass()), new Exception());
    }


}
