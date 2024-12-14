package n1luik.KAllFix.mixin.mixinfix.biolith.test_else;

import n1luik.KAllFix.fix.biolith.Fun_biolith;
import n1luik.K_multi_threading.core.Base;
import net.mcreator.terramity.init.TerramityModBiomes;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TerramityModBiomes.class)
public class TerramityModBiomesMixin {
    @Inject(method = "onServerAboutToStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;m_274409_()Lnet/minecraft/world/level/biome/Climate$ParameterList;"), remap = false)
    private static void fix1(ServerAboutToStartEvent event, CallbackInfo ci){
        Fun_biolith.fixBiomeSource(event.getServer().registryAccess());
    }
}
