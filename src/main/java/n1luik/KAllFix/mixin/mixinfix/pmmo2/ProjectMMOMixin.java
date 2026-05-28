package n1luik.KAllFix.mixin.mixinfix.pmmo2;

import harmonised.pmmo.ProjectMMO;
import n1luik.KAllFix.data.pmmo2.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProjectMMO.class,remap = false)
public class ProjectMMOMixin {
    @Inject(method = "<init>",at = @At("RETURN"), remap = false)
    private void onInit(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
    }
}