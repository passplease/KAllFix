package n1luik.K_multi_threading.neoforge.mixin.debug.sable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.heat.SubLevelHeatMapManager;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Slf4j
@Mixin(value = ServerSubLevel.class)
public abstract class ServerSubLevelMixin extends SubLevel implements PhysicsPipelineBody {
    @Shadow @Final private SubLevelHeatMapManager heatMapManager;

    protected ServerSubLevelMixin(Level level, int plotX, int plotY, Pose3d pose) {
        super(level, plotX, plotY, pose);
    }

    @Inject(method = "createPlot", at = @At("HEAD"))
    private void c1(SubLevelContainer plotContainer, int plotX, int plotY, int logPlotSize, CallbackInfoReturnable<LevelPlot> cir) {
        log.info("c3 {} {} {}", plotX, plotY, logPlotSize);
    }
    @Inject(method = "<init>", at = @At("RETURN"))
    private void i(ServerLevel level, int plotX, int plotY, Pose3d pose, CallbackInfo ci) {
        log.info("i {} {}", plotX, plotY, new Exception());
    }
    //@Inject(method = "tick", at = @At("HEAD"))
    //private void tick(CallbackInfo ci) {
    //    log.info("tick", new Exception());
    //}
    @Overwrite
    @ApiStatus.Internal
    public void tick() {
        super.tick();
        this.updateBoundingBox();
        BoundingBox3dc bounds = this.boundingBox();
        if (this.isRemoved() || !(bounds.minY() < SableConfig.SUB_LEVEL_REMOVE_MIN.getAsDouble()) && !(bounds.maxY() > SableConfig.SUB_LEVEL_REMOVE_MAX.getAsDouble())) {
            if (SableConfig.SUB_LEVEL_SPLITTING.getAsBoolean()) {
                this.heatMapManager.tick();
            }

        } else {
            Sable.LOGGER.info("Sub-level {} has an extreme Y coordinate range, removing {} {}", this, bounds.minY(), bounds.maxY());
            this.markRemoved();
        }
    }
}