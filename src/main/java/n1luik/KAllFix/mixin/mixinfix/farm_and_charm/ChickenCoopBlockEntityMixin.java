package n1luik.KAllFix.mixin.mixinfix.farm_and_charm;

import n1luik.KAllFix.Imixin.IOptimizeTag;
import n1luik.KAllFix.fix.farm_and_charm.FACOptimizeTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChickenCoopBlockEntity.class)
public class ChickenCoopBlockEntityMixin extends BlockEntity {

    public ChickenCoopBlockEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            IOptimizeTag chunkAt = (IOptimizeTag) this.level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(this.worldPosition.getX()), SectionPos.blockToSectionCoord(this.worldPosition.getZ()));
            if (chunkAt != null) {
                chunkAt.KAllFix$addOptimizeTag(FACOptimizeTag.ChickenCoopBlockTag);
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null) {
            IOptimizeTag chunkAt = (IOptimizeTag) this.level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(this.worldPosition.getX()), SectionPos.blockToSectionCoord(this.worldPosition.getZ()));
            if (chunkAt != null) {
                chunkAt.KAllFix$removeOptimizeTag(FACOptimizeTag.ChickenCoopBlockTag);
            }
        }
    }
}
