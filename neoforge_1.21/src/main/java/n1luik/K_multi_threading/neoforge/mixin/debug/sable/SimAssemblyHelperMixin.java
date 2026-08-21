package n1luik.K_multi_threading.neoforge.mixin.debug.sable;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Slf4j
@Mixin(value = SimAssemblyHelper.class)
public class SimAssemblyHelperMixin {

    @Inject(method = "assembleFromSingleBlock", at = @At("RETURN"))
    private static void a1(Level level, BlockPos selfPos, BlockPos toAssemble, boolean includeStart, boolean includeEncasingGlue, CallbackInfoReturnable<SimAssemblyHelper.AssemblyResult> cir) {
        log.info("a1 {} {}", selfPos, toAssemble);
    }
}