package n1luik.K_multi_threading.core.mixin.debug;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundKeepAlivePacket.class)
@Deprecated
public class ServerboundKeepAlivePacketDebug {
    @Inject(method = "<init>(J)V", at = @At("RETURN"))
    private void debug1(long p_132212_, CallbackInfo ci){
        Base.LOGGER.info("ServerboundKeepAlivePacket", "id", p_132212_);
    }
    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"))
    private void debug2(FriendlyByteBuf p_178895_, CallbackInfo ci){
        Base.LOGGER.info("ServerboundKeepAlivePacket", "id");
    }
}
