package n1luik.K_multi_threading.core.mixin.fix.rootoffear;

import com.alexander.rootoffear.capabilities.RoFLevelCapability;
import com.alexander.rootoffear.events.ServerUpdateClientEffectsEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ServerUpdateClientEffectsEvent.class, remap = false)
public class ServerUpdateClientEffectsEventFix1 {
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;m_8791_(Ljava/util/UUID;)Lnet/minecraft/world/entity/Entity;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void fix1(TickEvent.PlayerTickEvent event, CallbackInfo ci, ServerPlayer player, ServerLevel level, boolean fog, boolean music, RoFLevelCapability capability) {
        if (capability.getWiltedID() == null){
            ci.cancel();
        }
    }
}
