package n1luik.KAllFix.mixin.mixinfix.mpem;

import n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent;
import n1luik.K_multi_threading.core.Base;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.shuyanmc.mpem.ModEventProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = ModEventProcessor.class, remap = false)
public class ModEventProcessorMixin {
    @Inject(method = "processScanData", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fix1(ModFileScanData scanData, Set<String> eventMethods, CallbackInfo ci) {
        if (scanData.getIModInfoData().stream().anyMatch(modInfoData -> {
            return modInfoData.getMods().stream().anyMatch(mod -> {
                System.out.println(mod.getModId());
                return mod.getModId().equals("k_all_fix");
            });
        })) {

            ci.cancel();
        }
    }

}