package n1luik.KAllFix.mixin.mixinfix.path.cataclysm;


import com.github.L_Ender.cataclysm.event.ServerEventHandler;
import n1luik.KAllFix.Imixin.mod.cataclysm.ICataclysmLivingEntityCapability;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerEventHandler.class)
public class ServerEventHandlerMixin {
    //内连
    @Redirect(method = "onLivingUpdateEvent", at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/init/ModCapabilities;getCapability(Lnet/minecraft/world/entity/Entity;Lnet/minecraftforge/common/capabilities/Capability;)Ljava/lang/Object;", ordinal = 0, remap = false), remap = false)
    private  <T> T fix1(Entity entity, Capability<T> capability) {
        return (T)((ICataclysmLivingEntityCapability)entity).KAllFix$cataclysm$getHookCapability();
    }
    @Redirect(method = "onLivingUpdateEvent", at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/init/ModCapabilities;getCapability(Lnet/minecraft/world/entity/Entity;Lnet/minecraftforge/common/capabilities/Capability;)Ljava/lang/Object;", ordinal = 1, remap = false), remap = false)
    private <T> T fix2(Entity entity, Capability<T> capability) {
        return (T)((ICataclysmLivingEntityCapability)entity).KAllFix$cataclysm$getChargeCapability();
    }
    @Redirect(method = "onLivingUpdateEvent", at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/init/ModCapabilities;getCapability(Lnet/minecraft/world/entity/Entity;Lnet/minecraftforge/common/capabilities/Capability;)Ljava/lang/Object;", ordinal = 2, remap = false), remap = false)
    private <T> T fix3(Entity entity, Capability<T> capability) {
        return (T)((ICataclysmLivingEntityCapability)entity).KAllFix$cataclysm$getRenderRushCapability();
    }
}
