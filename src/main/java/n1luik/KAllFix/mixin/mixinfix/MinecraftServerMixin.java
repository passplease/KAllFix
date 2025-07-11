package n1luik.KAllFix.mixin.mixinfix;

import n1luik.KAllFix.fix.biolith.Fun_biolith;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void fix1(CallbackInfo ci) {
        Base.mcs = (MinecraftServer)(Object)this;
    }
}
