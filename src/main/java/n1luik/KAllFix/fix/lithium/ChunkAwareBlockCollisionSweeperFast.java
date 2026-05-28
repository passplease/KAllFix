package n1luik.KAllFix.fix.lithium;

import com.google.common.collect.AbstractIterator;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChunkAwareBlockCollisionSweeperFast// extends AbstractIterator<VoxelShape>
{
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
    private final double scanDistance; // 动态扫描距离
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

    public ChunkAwareBlockCollisionSweeperFast(Level world, Entity entity, AABB box) {
        this.box = box;
        this.shape = Shapes.create(box);
        this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
        // 动态计算扫描距离：根据实体移动速度和尺寸
        if (entity != null) {
            double speed = entity.getDeltaMovement().length(); // 正确
            double size = Math.max(box.getXsize(), Math.max(box.getYsize(), box.getZsize()));
            this.scanDistance = Math.min(5.0, size + Math.max(1.0, speed * 0.5));
        } else {
            this.scanDistance = 3.0;
        }

        // 仅扫描实体周围的动态范围
        double centerX = (box.minX + box.maxX) / 2.0;
        double centerY = (box.minY + box.maxY) / 2.0;
        double centerZ = (box.minZ + box.maxZ) / 2.0;

        this.minX = Mth.floor(centerX - scanDistance);
        this.maxX = Mth.floor(centerX + scanDistance);
        this.minY = Mth.floor(centerY - scanDistance);
        this.maxY = Mth.floor(centerY + scanDistance);
        this.minZ = Mth.floor(centerZ - scanDistance);
        this.maxZ = Mth.floor(centerZ + scanDistance);

        this.world = world;
        //this.minX = Mth.floor(box.minX - 1.0E-7);
        //this.maxX = Mth.floor(box.maxX + 1.0E-7);
        //this.minY = Mth.clamp(Mth.floor(box.minY - 1.0E-7), BlockCoord.getMinY(this.world), BlockCoord.getMaxYInclusive(this.world));
        //this.maxY = Mth.clamp(Mth.floor(box.maxY + 1.0E-7), BlockCoord.getMinY(this.world), BlockCoord.getMaxYInclusive(this.world));
        //this.minZ = Mth.floor(box.minZ - 1.0E-7);
        //this.maxZ = Mth.floor(box.maxZ + 1.0E-7);
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
                // 2. **关键修复：检查整个区块是否在扫描范围内（X/Y/Z三方向）**
                // 计算当前区块的物理坐标范围
                int chunkStartX = this.chunkX * 16;
                int chunkEndX = chunkStartX + 15;
                int chunkStartZ = this.chunkZ * 16;
                int chunkEndZ = chunkStartZ + 15;
                int sectionStartY = BlockCoord.getMinYInSectionIndex(this.world, this.chunkYIndex);
                int sectionEndY = sectionStartY + 15;
                // 检查X方向：区块完全在扫描范围左侧或右侧
                if (chunkEndX < this.minX || chunkStartX > this.maxX) continue;
                // 检查Z方向：区块完全在扫描范围前方或后方
                if (chunkEndZ < this.minZ || chunkStartZ > this.maxZ) continue;
                // 检查Y方向：区块完全在扫描范围下方或上方
                if (sectionEndY < this.minY || sectionStartY > this.maxY) continue;
                // 3. 跳过无效区块（空chunk/空section）
                if (this.cachedChunk == null || this.cachedChunkSection == null || this.cachedChunkSection.hasOnlyAir()) {
                    continue;
                }

//                // 4. 设置当前区块的迭代范围（使用动态扫描范围）
//                this.sectionOversizedBlocks = hasChunkSectionOversizedBlocks(this.cachedChunk, this.chunkYIndex);
//                int sizeExtension = this.sectionOversizedBlocks ? 1 : 0;
//                // 仅扫描动态范围内的部分（不再是整个box）
//                this.cEndX = Math.min(this.maxX + sizeExtension, Pos.BlockCoord.getMaxInSectionCoord(this.chunkX));
//                int cEndY = Math.min(this.maxY + sizeExtension, Pos.BlockCoord.getMaxYInSectionIndex(this.world, this.chunkYIndex));
//                this.cEndZ = Math.min(this.maxZ + sizeExtension, Pos.BlockCoord.getMaxInSectionCoord(this.chunkZ));
//
//                this.cStartX = Math.max(this.minX - sizeExtension, Pos.BlockCoord.getMinInSectionCoord(this.chunkX));
//                int cStartY = Math.max(this.minY - sizeExtension, Pos.BlockCoord.getMinYInSectionIndex(this.world, this.chunkYIndex));
//                this.cStartZ = Math.max(this.minZ - sizeExtension, Pos.BlockCoord.getMinInSectionCoord(this.chunkZ));
//                this.cTotalSize = (this.cEndX - this.cStartX + 1) * (cEndY - cStartY + 1) * (this.cEndZ - this.cStartZ + 1);
//                if (this.cTotalSize <= 0) continue; // 跳过空范围
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
