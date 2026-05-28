package n1luik.KAllFix.mixin.mixinfix.lithium;

import me.jellysquid.mods.lithium.common.entity.LithiumEntityCollisions;
import me.jellysquid.mods.lithium.common.entity.movement.ChunkAwareBlockCollisionSweeper;
import n1luik.KAllFix.fix.lithium.ChunkAwareBlockCollisionSweeperFast;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = LithiumEntityCollisions.class, remap = false)
public class LithiumEntityCollisionsMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static List<VoxelShape> getBlockCollisions(Level world, Entity entity, AABB box) {
        return (new ChunkAwareBlockCollisionSweeperFast(world, entity, box)).collectAll();
    }
}