package n1luik.KAllFix.mixin.mixinfix.farm_and_charm;

import n1luik.KAllFix.Imixin.IKAFSaturationTracker;
import net.minecraft.world.entity.Entity;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static n1luik.KAllFix.data.farm_and_charm.Event.removeO1;

@Mixin(value = PacketHandler.class, remap = false)
public class PacketHandlerMixin {
    @Inject(method = "sendSaturationSync", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fix1(SyncSaturationPacket packet, Entity entity, CallbackInfo ci) {
        if (removeO1.get() != 0) {
            return;
        }
        //增加兼容
        if (((IKAFSaturationTracker)((SaturationTracker.SaturatedAnimal)entity).farm_and_charm$getSaturationTracker()).KAllFix$testSendData(packet.getFoodCounter())) {
            ci.cancel();
        }
    }
}
