package n1luik.K_multi_threading.core.mixin.fix.ic2c;

import ic2.core.utils.SidedGateway;
import n1luik.K_multi_threading.core.Base;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(SidedGateway.class)
public class SidedGatewayFix1<T> {
    @Shadow(remap = false) @Final
    private T serverInstance;

    @Shadow(remap = false) @Final @Mutable
    private T clientInstance;

    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"), remap = false)
    public void debug1(String server, String client, CallbackInfo ci) {
        if (clientInstance == null) clientInstance = serverInstance;
    }

    @Inject(method = "<init>(Ljava/lang/Class;Ljava/lang/Class;)V", at = @At("RETURN"), remap = false)
    public void debug2(Class server, Class client, CallbackInfo ci) {
        if (clientInstance == null) clientInstance = serverInstance;
    }

    @Inject(method = "<init>(Ljava/util/function/Supplier;Ljava/util/function/Supplier;)V", at = @At("RETURN"), remap = false)
    public void debug3(Supplier server, Supplier client, CallbackInfo ci) {
        if (clientInstance == null) clientInstance = serverInstance;
    }


}
