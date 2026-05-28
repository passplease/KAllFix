package n1luik.KAllFix.mixin.mixinfix.lithium;

import me.jellysquid.mods.lithium.common.entity.movement.ChunkAwareBlockCollisionSweeper;
import me.jellysquid.mods.lithium.common.shapes.VoxelShapeCaster;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ChunkAwareBlockCollisionSweeper.class, remap = false)
public class ChunkAwareBlockCollisionSweeperMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private static VoxelShape getCollidedShape(AABB entityBox, VoxelShape entityShape, VoxelShape shape, int x, int y, int z) {
        if (shape == Shapes.block()) {
            return entityBox.intersects((double)x, (double)y, (double)z, (double)x + (double)1.0F, (double)y + (double)1.0F, (double)z + (double)1.0F) ?
                    shape.move((double)x, (double)y, (double)z) : null;
        } else if (shape instanceof VoxelShapeCaster) {
            return ((VoxelShapeCaster)shape).intersects(entityBox, (double)x, (double)y, (double)z) ? shape.move((double)x, (double)y, (double)z) : null;
        } else {
            shape = shape.move((double)x, (double)y, (double)z);

            if (!entityBox.intersects(shape.bounds())) return null; // 避免复杂形状检测
            return Shapes.joinIsNotEmpty(shape, entityShape, BooleanOp.AND) ? shape : null;
        }
    }
}