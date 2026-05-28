package n1luik.KAllFix.fix.lithium;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import me.jellysquid.mods.lithium.common.block.BlockCountingSection;
import me.jellysquid.mods.lithium.common.block.BlockStateFlags;
import me.jellysquid.mods.lithium.common.shapes.VoxelShapeCaster;
import me.jellysquid.mods.lithium.common.util.Pos.BlockCoord;
import me.jellysquid.mods.lithium.common.util.Pos.ChunkCoord;
import me.jellysquid.mods.lithium.common.util.Pos.SectionYIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// TODO 待引用
public class ChunkAwareBlockCollisionSweeperV2 //extends AbstractIterator<VoxelShape>
{
    @AllArgsConstructor
    public static class RootBufData{
        //弱引用缓存区块
        public final int cachedChunkSize = 8;
        private final WeakReference<ChunkAccess>[] cachedChunkAccess = new WeakReference[cachedChunkSize];
        private final long[] cachedChunkAccessPos = new long[cachedChunkSize];
        private final ResourceKey<Level>[] dimension = new ResourceKey[cachedChunkSize];
        private int setOff = 0;
        public final ChunkAccess getChunk_(int id, ResourceKey<Level> w, long pos){
            if (cachedChunkAccessPos[id] == pos){
                if (dimension[id] == w){
                    var d = cachedChunkAccess[id];
                    if (d != null){
                        ChunkAccess chunkAccess = d.get();
                        if (chunkAccess == null){
                            cachedChunkAccess[id] = null;
                        }
                        return chunkAccess;
                    }
                }
            }
            return null;
        }
        public final void addBuf(ChunkAccess c, ResourceKey<Level> w, long pos){
            if (cachedChunkAccess[0] == null){
                cachedChunkAccess[0] = new WeakReference<>(c);
                cachedChunkAccessPos[0] = pos;
                dimension[0] = w;
            }
            if (cachedChunkAccess[1] == null){
                cachedChunkAccess[1] = new WeakReference<>(c);
                cachedChunkAccessPos[1] = pos;
                dimension[1] = w;
            }
            if (cachedChunkAccess[2] == null){
                cachedChunkAccess[2] = new WeakReference<>(c);
                cachedChunkAccessPos[2] = pos;
                dimension[2] = w;
            }
            if (cachedChunkAccess[3] == null){
                cachedChunkAccess[3] = new WeakReference<>(c);
                cachedChunkAccessPos[3] = pos;
                dimension[3] = w;
            }
            if (cachedChunkAccess[4] == null){
                cachedChunkAccess[4] = new WeakReference<>(c);
                cachedChunkAccessPos[4] = pos;
                dimension[4] = w;
            }
            if (cachedChunkAccess[5] == null){
                cachedChunkAccess[5] = new WeakReference<>(c);
                cachedChunkAccessPos[5] = pos;
                dimension[5] = w;
            }
            if (cachedChunkAccess[6] == null){
                cachedChunkAccess[6] = new WeakReference<>(c);
                cachedChunkAccessPos[6] = pos;
                dimension[6] = w;
            }
            if (cachedChunkAccess[7] == null){
                cachedChunkAccess[7] = new WeakReference<>(c);
                cachedChunkAccessPos[7] = pos;
                dimension[7] = w;
            }
            setOff++;
            setOff = setOff % cachedChunkSize;
            cachedChunkAccess[setOff] = new WeakReference<>(c);
            cachedChunkAccessPos[setOff] = pos;
            dimension[setOff] = w;
        }
        public final ChunkAccess getChunk(Level w, int x, int z){
            var w2 = w.dimension;
            long pos2 = ChunkPos.asLong(x, z);
            var ret = getChunk_(0, w2, pos2);
            if (ret != null) return ret;
            ret = getChunk_(1, w2, pos2);
            if (ret != null) return ret;
            ret = getChunk_(2, w2, pos2);
            if (ret != null) return ret;
            ret = getChunk_(3, w2, pos2);
            if (ret != null) return ret;
            ret = getChunk_(4, w2, pos2);
            if (ret != null) return ret;
            ret = getChunk_(5, w2, pos2);
            if (ret != null) return ret;
            ret = getChunk_(6, w2, pos2);
            if (ret != null) return ret;
            ret = getChunk_(7, w2, pos2);
            if (ret != null) return ret;
            ret = w.getChunk(x, z, ChunkStatus.FULL, false);
            if (ret == null)return ret;
            addBuf(ret, w2, pos2);
            return ret;
        }

    }
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    private final AABB box;
    private final VoxelShape shape;
    private final Level world;
    private final CollisionContext context;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private int chunkX;
    private int chunkYIndex;
    private int chunkZ;
    private int cStartX;
    private int cStartZ;
    private int cEndX;
    private int cEndZ;
    private int cX;
    private int cY;
    private int cZ;
    private int maxHitX;
    private int maxHitY;
    private int maxHitZ;
    private int maxIndex;
    private int index;
    private int cTotalSize;
    private int cIterated;
    private boolean sectionOversizedBlocks;
    private ChunkAccess cachedChunk;
    private LevelChunkSection cachedChunkSection;

    public ChunkAwareBlockCollisionSweeperV2(Level world, Entity entity, AABB box) {
        this.box = box;
        this.shape = Shapes.create(box);
        this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
        this.world = world;
        this.minX = Mth.floor(box.minX - 1.0E-7);
        this.maxX = Mth.floor(box.maxX + 1.0E-7);
        this.minY = Mth.clamp(Mth.floor(box.minY - 1.0E-7), BlockCoord.getMinY(this.world), BlockCoord.getMaxYInclusive(this.world));
        this.maxY = Mth.clamp(Mth.floor(box.maxY + 1.0E-7), BlockCoord.getMinY(this.world), BlockCoord.getMaxYInclusive(this.world));
        this.minZ = Mth.floor(box.minZ - 1.0E-7);
        this.maxZ = Mth.floor(box.maxZ + 1.0E-7);
        this.chunkX = ChunkCoord.fromBlockCoord(expandMin(this.minX));
        this.chunkZ = ChunkCoord.fromBlockCoord(expandMin(this.minZ));
        this.cIterated = 0;
        this.cTotalSize = 0;
        this.maxHitX = Integer.MIN_VALUE;
        this.maxHitY = Integer.MIN_VALUE;
        this.maxHitZ = Integer.MIN_VALUE;
        this.maxIndex = Integer.MIN_VALUE;
        this.index = 0;
        --this.chunkX;
    }

    private boolean nextSection() {
        while(true) {
            if (this.cachedChunk != null && this.chunkYIndex < SectionYIndex.getMaxYSectionIndexInclusive(this.world) && this.chunkYIndex < SectionYIndex.fromBlockCoord(this.world, expandMax(this.maxY))) {
                ++this.chunkYIndex;
                this.cachedChunkSection = this.cachedChunk.getSections()[this.chunkYIndex];
            } else {
                this.chunkYIndex = Mth.clamp(SectionYIndex.fromBlockCoord(this.world, expandMin(this.minY)), SectionYIndex.getMinYSectionIndex(this.world), SectionYIndex.getMaxYSectionIndexInclusive(this.world));
                if (this.chunkX < ChunkCoord.fromBlockCoord(expandMax(this.maxX))) {
                    ++this.chunkX;
                } else {
                    this.chunkX = ChunkCoord.fromBlockCoord(expandMin(this.minX));
                    if (this.chunkZ >= ChunkCoord.fromBlockCoord(expandMax(this.maxZ))) {
                        return false;
                    }

                    ++this.chunkZ;
                }

                this.cachedChunk = this.world.getChunk(this.chunkX, this.chunkZ, ChunkStatus.FULL, false);
                if (this.cachedChunk != null) {
                    this.cachedChunkSection = this.cachedChunk.getSections()[this.chunkYIndex];
                }
            }

            if (this.cachedChunk != null && this.cachedChunkSection != null && !this.cachedChunkSection.hasOnlyAir()) {
                this.sectionOversizedBlocks = hasChunkSectionOversizedBlocks(this.cachedChunk, this.chunkYIndex);
                int sizeExtension = this.sectionOversizedBlocks ? 1 : 0;
                this.cEndX = Math.min(this.maxX + sizeExtension, BlockCoord.getMaxInSectionCoord(this.chunkX));
                int cEndY = Math.min(this.maxY + sizeExtension, BlockCoord.getMaxYInSectionIndex(this.world, this.chunkYIndex));
                this.cEndZ = Math.min(this.maxZ + sizeExtension, BlockCoord.getMaxInSectionCoord(this.chunkZ));
                this.cStartX = Math.max(this.minX - sizeExtension, BlockCoord.getMinInSectionCoord(this.chunkX));
                int cStartY = Math.max(this.minY - sizeExtension, BlockCoord.getMinYInSectionIndex(this.world, this.chunkYIndex));
                this.cStartZ = Math.max(this.minZ - sizeExtension, BlockCoord.getMinInSectionCoord(this.chunkZ));
                this.cX = this.cStartX;
                this.cY = cStartY;
                this.cZ = this.cStartZ;
                this.cTotalSize = (this.cEndX - this.cStartX + 1) * (cEndY - cStartY + 1) * (this.cEndZ - this.cStartZ + 1);
                if (this.cTotalSize != 0) {
                    this.cIterated = 0;
                    return true;
                }
            }
        }
    }

    public VoxelShape computeNext() {
        while(this.cIterated < this.cTotalSize || this.nextSection()) {
            ++this.cIterated;
            int x = this.cX;
            int y = this.cY;
            int z = this.cZ;
            if (this.cX < this.cEndX) {
                ++this.cX;
            } else if (this.cZ < this.cEndZ) {
                this.cX = this.cStartX;
                ++this.cZ;
            } else {
                this.cX = this.cStartX;
                this.cZ = this.cStartZ;
                ++this.cY;
            }

            int edgesHit = this.sectionOversizedBlocks ? (x >= this.minX && x <= this.maxX ? 0 : 1) + (y >= this.minY && y <= this.maxY ? 0 : 1) + (z >= this.minZ && z <= this.maxZ ? 0 : 1) : 0;
            if (edgesHit != 3) {
                BlockState state = this.cachedChunkSection.getBlockState(x & 15, y & 15, z & 15);
                if (canInteractWithBlock(state, edgesHit)) {
                    this.pos.set(x, y, z);
                    VoxelShape collisionShape = state.getCollisionShape(this.world, this.pos, this.context);
                    if (collisionShape != Shapes.empty() && collisionShape != null) {
                        VoxelShape collidedShape = getCollidedShape(this.box, this.shape, collisionShape, x, y, z);
                        if (collidedShape != null) {
                            if (z >= this.maxHitZ && (z > this.maxHitZ || y >= this.maxHitY && (y > this.maxHitY || x > this.maxHitX))) {
                                this.maxHitX = x;
                                this.maxHitY = y;
                                this.maxHitZ = z;
                                this.maxIndex = this.index;
                            }

                            ++this.index;
                            return collidedShape;
                        }
                    }
                }
            }
        }

        return null;//(VoxelShape)this.endOfData();
    }

    private static boolean canInteractWithBlock(BlockState state, int edgesHit) {
        return (edgesHit != 1 || state.hasLargeCollisionShape()) && (edgesHit != 2 || state.getBlock() == Blocks.MOVING_PISTON);
    }

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

    private static int expandMin(int coord) {
        return coord - 1;
    }

    private static int expandMax(int coord) {
        return coord + 1;
    }

    private static boolean hasChunkSectionOversizedBlocks(ChunkAccess chunk, int chunkY) {
        if (!BlockStateFlags.ENABLED) {
            return true;
        } else {
            LevelChunkSection section = chunk.getSections()[chunkY];
            return section != null && ((BlockCountingSection)section).mayContainAny(BlockStateFlags.OVERSIZED_SHAPE);
        }
    }

    public List<VoxelShape> collectAll() {
        ArrayList<VoxelShape> collisions = new ArrayList();

        VoxelShape buf = null;
        while(true) {
            buf = this.computeNext();
            if (buf == null)break;
            collisions.add(buf);
        }

        if (collisions.size() >= 2) {
            collisions.set(this.maxIndex, (VoxelShape)collisions.set(collisions.size() - 1, (VoxelShape)collisions.get(this.maxIndex)));
        }

        return collisions;
    }
}
