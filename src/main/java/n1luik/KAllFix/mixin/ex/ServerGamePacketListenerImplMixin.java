package n1luik.KAllFix.mixin.ex;

import net.minecraft.Util;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Unique
    private static final long KEEP_TIME = Long.getLong("KAF-ClientboundKeepAlivePacket_Max", 15000L);
    //@ModifyConstant(method = "tick", constant = @Constant(longValue = 15000L))
    //public long fix1(long constant){
    //    return Long.getLong("KAF-ClientboundKeepAlivePacket_Max", 15000L);
    //}
    @ModifyConstant(method = "tick", constant = @Constant(longValue = 15000L))
    public long fix2(long constant){
        return KEEP_TIME;
    }
}
