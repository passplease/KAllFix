package n1luik.K_multi_threading.core.mixin.k;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Unique
    private static final Logger k_multi_threading$logger = LoggerFactory.getLogger("[handleDisconnection-debug]");
    @Shadow private Channel channel;

    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    public void debug1(CallbackInfo ci){
        if (this.channel != null && !this.channel.isOpen()) {
            StackWalker.getInstance().forEach(v -> {
                k_multi_threading$logger.info(v.toString());
            });
        }else {
            StackWalker.getInstance().forEach(v -> {
                k_multi_threading$logger.debug(v.toString());
            });
        }
    }
}
