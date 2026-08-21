package n1luik.K_multi_threading.neoforge.mixin.fix.rpc;

import cn.chloeprime.commons.rpc.RPCTarget;
import cn.chloeprime.commons.rpc.exception.UnsupportedRpcOperationException;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = RPCTarget.class)
public class RPCTargetMixin {

    @Overwrite
    @ApiStatus.Internal
    public static void checkCallingSide(LogicalSide required) {
    }
}