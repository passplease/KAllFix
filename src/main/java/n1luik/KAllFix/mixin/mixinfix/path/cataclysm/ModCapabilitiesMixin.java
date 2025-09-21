package n1luik.KAllFix.mixin.mixinfix.path.cataclysm;


import com.github.L_Ender.cataclysm.init.ModCapabilities;
import n1luik.KAllFix.Imixin.mod.cataclysm.ICataclysmLivingEntityCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ModCapabilities.class)
public class ModCapabilitiesMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Nullable
    public static <T> T getCapability(Entity entity, Capability<T> capability) {
        if (entity == null) {
            return null;
        } else if (!entity.isAlive()) {
            return null;
        } else {
            LazyOptional<T> optional = entity.getCapability(capability);
            return (optional.isPresent() ? optional.orElseThrow(() -> new IllegalArgumentException("Lazy optional must not be empty")) : null);
        }
    }
    @Inject(method = "attachEntityCapability", at = @At("RETURN"), remap = false)
    private static void KAllFix$fix1(AttachCapabilitiesEvent<Entity> e, CallbackInfo ci) {
        if (e.getObject() instanceof LivingEntity living) {
            ICataclysmLivingEntityCapability cap = (ICataclysmLivingEntityCapability) living;
            ICapabilityProvider iCapabilityProvider = e.getCapabilities().get(ModCapabilities.HOOK_CAPABILITY);
            if (iCapabilityProvider != null) {
                cap.KAllFix$cataclysm$setHookCapability(iCapabilityProvider.getCapability(ModCapabilities.HOOK_CAPABILITY));
            }
            ICapabilityProvider iCapabilityProvider2 = e.getCapabilities().get(ModCapabilities.CHARGE_CAPABILITY);
            if (iCapabilityProvider2 != null) {
                cap.KAllFix$cataclysm$setChargeCapability(iCapabilityProvider2.getCapability(ModCapabilities.CHARGE_CAPABILITY));
            }
            ICapabilityProvider iCapabilityProvider3 = e.getCapabilities().get(ModCapabilities.RENDER_RUSH_CAPABILITY);
            if (iCapabilityProvider3 != null) {
                cap.KAllFix$cataclysm$setRenderRushCapability(iCapabilityProvider3.getCapability(ModCapabilities.RENDER_RUSH_CAPABILITY));
            }
        }
    }
}
