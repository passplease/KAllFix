package n1luik.KAllFix.data.upgradedcore;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class TeleportUtil2 {
    public static boolean isSafeSpot(Level level, BlockPos blockPos) {
        BlockPos above = blockPos.above();
        BlockState blockState = level.getBlockState(above);
        return level.getBlockState(blockPos.below()).blocksMotion() &&
                (level.getFluidState(blockPos).isEmpty() ||
                        level.getBlockState(blockPos).is(Blocks.BUBBLE_COLUMN))
                && level.getBlockState(blockPos).isPathfindable(level, blockPos, PathComputationType.LAND)
                && (level.getFluidState(above).isEmpty() ||
                blockState.is(Blocks.BUBBLE_COLUMN)) &&
                blockState.isPathfindable(level, above, PathComputationType.LAND);
    }
    public static boolean isSafeSpot(Level level, ChunkAccess chunk, BlockPos blockPos) {
        BlockPos above = blockPos.above();
        BlockState blockState = chunk.getBlockState(above);
        return chunk.getBlockState(blockPos.below()).blocksMotion() &&
                (chunk.getFluidState(blockPos).isEmpty() ||
                        chunk.getBlockState(blockPos).is(Blocks.BUBBLE_COLUMN))
                && chunk.getBlockState(blockPos).isPathfindable(level, blockPos, PathComputationType.LAND)
                && (chunk.getFluidState(above).isEmpty() ||
                blockState.is(Blocks.BUBBLE_COLUMN)) &&
                blockState.isPathfindable(level, above, PathComputationType.LAND);
    }
    public static boolean isSafeSpot(Level level, ChunkAccess chunk, BlockPos.MutableBlockPos blockPos) {
        if ((chunk.getFluidState(blockPos).isEmpty() ||
                        chunk.getBlockState(blockPos).is(Blocks.BUBBLE_COLUMN))
                        && chunk.getBlockState(blockPos).isPathfindable(level, blockPos, PathComputationType.LAND)) {
            blockPos.setY(blockPos.getY() - 1);
            if (!chunk.getBlockState(blockPos.below()).blocksMotion()) {
                return false;
            }
            blockPos.setY(blockPos.getY() + 2);
            BlockState blockState = chunk.getBlockState(blockPos);
            return  (chunk.getFluidState(blockPos).isEmpty() ||
                    blockState.is(Blocks.BUBBLE_COLUMN)) &&
                    blockState.isPathfindable(level, blockPos, PathComputationType.LAND);
        }
        return false;
    }
    public static boolean isSafeSpotR(Level level, ChunkAccess chunk, BlockPos.MutableBlockPos blockPos) {
        if ((chunk.getFluidState(blockPos).isEmpty() ||
                        chunk.getBlockState(blockPos).is(Blocks.BUBBLE_COLUMN))
                        && chunk.getBlockState(blockPos).isPathfindable(level, blockPos, PathComputationType.LAND)) {
            blockPos.setY(blockPos.getY() - 1);
            if (!chunk.getBlockState(blockPos.below()).blocksMotion()) {
                blockPos.setY(blockPos.getY() + 1);
                return false;
            }
            blockPos.setY(blockPos.getY() + 2);
            BlockState blockState = chunk.getBlockState(blockPos);
            boolean b = (chunk.getFluidState(blockPos).isEmpty() ||
                    blockState.is(Blocks.BUBBLE_COLUMN)) &&
                    blockState.isPathfindable(level, blockPos, PathComputationType.LAND);
            blockPos.setY(blockPos.getY() - 1);
            return b;
        }
        return false;
    }
}
