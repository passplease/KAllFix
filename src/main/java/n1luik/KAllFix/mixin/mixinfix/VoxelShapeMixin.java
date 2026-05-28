package n1luik.KAllFix.mixin.mixinfix;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = VoxelShape.class, priority = 900)
public abstract class VoxelShapeMixin {
    @Shadow public abstract boolean isEmpty();

    @Shadow public abstract double min(Direction.Axis p_83289_);

    @Shadow public abstract double max(Direction.Axis p_83298_);

    @Unique
    private AABB aabbBuf = null;
    /**
     * @author
     * @reason
     */
    @Overwrite
    public AABB bounds() {
        if (aabbBuf != null) {
            return aabbBuf;
        }
        if (this.isEmpty()) {
            throw (UnsupportedOperationException) Util.pauseInIde(new UnsupportedOperationException("No bounds for empty shape."));
        } else {
            return aabbBuf = new AABB(this.min(Direction.Axis.X), this.min(Direction.Axis.Y), this.min(Direction.Axis.Z), this.max(Direction.Axis.X), this.max(Direction.Axis.Y), this.max(Direction.Axis.Z));
        }
    }
}
