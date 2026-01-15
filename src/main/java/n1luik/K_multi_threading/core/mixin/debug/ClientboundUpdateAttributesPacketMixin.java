package n1luik.K_multi_threading.core.mixin.debug;

import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Deprecated
@Mixin(value = ClientboundUpdateAttributesPacket.class)
public class ClientboundUpdateAttributesPacketMixin {
    @Inject(method = "<init>(ILjava/util/Collection;)V", at = @At("RETURN"))
    public void debug1(int p_133580_, Collection p_133581_, CallbackInfo ci){
        new Throwable().printStackTrace();
    }
}