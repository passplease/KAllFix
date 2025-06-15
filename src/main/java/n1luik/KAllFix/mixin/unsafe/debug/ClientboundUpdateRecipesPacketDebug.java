package n1luik.KAllFix.mixin.unsafe.debug;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ClientboundUpdateRecipesPacket.class)
public class ClientboundUpdateRecipesPacketDebug {
    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"))
    public void debug1(FriendlyByteBuf p_179468_, CallbackInfo ci) {
        Base.LOGGER.info("ClientboundUpdateRecipesPacketDebug: <init>", new Throwable());
    }
    @Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("RETURN"))
    public void debug2(Collection p_133632_, CallbackInfo ci) {
        Base.LOGGER.info("ClientboundUpdateRecipesPacketDebug: <init>", new Throwable());
    }
}
