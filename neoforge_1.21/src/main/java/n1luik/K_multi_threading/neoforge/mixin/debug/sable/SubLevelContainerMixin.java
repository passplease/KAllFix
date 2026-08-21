package n1luik.K_multi_threading.neoforge.mixin.debug.sable;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import lombok.extern.slf4j.Slf4j;
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
@Mixin(value = SubLevelContainer.class)
public class SubLevelContainerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void i(Level level, int logSideLength, int logPlotSize, int originX, int originZ, CallbackInfo ci) {
        log.info("i2 {} {} {} {} {}", level.dimension(), logSideLength, logPlotSize, originX, originZ, new Exception());
    }
    @Inject(method = "allocateNewSubLevel", at = @At("RETURN"))
    private void i(Pose3d pose, CallbackInfoReturnable<SubLevel> cir) {
        log.info("c1 {}", pose.position(), new Exception());
    }
}