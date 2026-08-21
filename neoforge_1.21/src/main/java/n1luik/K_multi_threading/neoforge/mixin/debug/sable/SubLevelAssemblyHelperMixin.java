package n1luik.K_multi_threading.neoforge.mixin.debug.sable;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Slf4j
@Mixin(value = SubLevelAssemblyHelper.class)
public class SubLevelAssemblyHelperMixin {

    @Inject(method = "assembleBlocks", at = @At("RETURN"))
    private static void a1(ServerLevel level, BlockPos anchor, Iterable<BlockPos> blocks, BoundingBox3ic bounds, CallbackInfoReturnable<ServerSubLevel> cir) {
        log.info("a2 {} {}", anchor,blocks);
    }
}