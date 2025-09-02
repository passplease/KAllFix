package n1luik.KAllFix.mixin.mixinfix.farm_and_charm;

import n1luik.KAllFix.forge.ModInit;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModInit.class)
public class This {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(n1luik.KAllFix.data.farm_and_charm.Event.class);

    }
}
