package n1luik.K_multi_threading.neoforge.mixin.debug.sable;

import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Slf4j
@Mixin(value = LevelPlot.class)
@Deprecated
public class LevelPlotMixin {

    @Shadow @Final public ChunkPos plotPos;

    @Inject(method = "newEmptyChunk", at = @At("HEAD"))
    private void n1(ChunkPos pos, CallbackInfo ci) {
        log.info("n1 {} {}", pos, plotPos);
    }
}