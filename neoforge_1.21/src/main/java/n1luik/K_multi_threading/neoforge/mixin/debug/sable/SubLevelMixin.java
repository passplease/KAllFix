package n1luik.K_multi_threading.neoforge.mixin.debug.sable;

import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4d;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Slf4j
@Mixin(value = SubLevel.class)
@Deprecated
public class SubLevelMixin {
    @Shadow @Final private LevelPlot plot;

    @Shadow @Final protected BoundingBox3d globalBounds;

    @Shadow @Final protected BoundingBox3d lastGlobalBounds;

    @Shadow @Final private Matrix4d globalBoundsTransform;

    @Shadow @Final private Pose3d pose;

    @Overwrite
    public void updateBoundingBox() {
        BoundingBox3ic plotBounds = this.plot.getBoundingBox();

        assert plotBounds != null : "Plot bounds are null";
        for (PlotChunkHolder loadedChunk : this.plot.getLoadedChunks()) {
            if (loadedChunk.getBoundingBox() == null) log.info("null chunk {}", loadedChunk);
        }
        log.info("updateBoundingBox {} {} {} {}", plotBounds.minX(), plotBounds.minY(), ((List)this.plot.getLoadedChunks()).size(), plotBounds == BoundingBox3i.EMPTY);
        log.info("globalBounds {} {}", globalBounds.minX(), globalBounds.minY());

        this.lastGlobalBounds.set(this.globalBounds);
        this.globalBounds.set((double)plotBounds.minX(), (double)plotBounds.minY(), (double)plotBounds.minZ(), (double)plotBounds.maxX() + (double)1.0F, (double)plotBounds.maxY() + (double)1.0F, (double)plotBounds.maxZ() + (double)1.0F);
        this.globalBounds.transform(this.pose, this.globalBoundsTransform, this.globalBounds);
    }
}