package n1luik.K_multi_threading.core.mixin.fix.extendedae_plus;

import appeng.blockentity.AEBaseBlockEntity;
import com.extendedae_plus.content.wireless.WirelessTransceiverBlockEntity;
import n1luik.K_multi_threading.core.Imixin.IChunkMap;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WirelessTransceiverBlockEntity.class, remap = false)
public class WirelessTransceiverBlockEntityFix1 extends AEBaseBlockEntity {
    public WirelessTransceiverBlockEntityFix1(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }
    @Redirect(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_7731_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", remap = false), remap = false)
    public boolean fix1(Level instance, BlockPos blockPos, BlockState blockState, int i){
        if (instance.getChunkSource() instanceof ParaServerChunkProvider chunkCache) {
            if (((IChunkMap)chunkCache.chunkMap).KAK$Unloads()){
                chunkCache.KMT$addTickRun(()->instance.setBlock(blockPos, blockState, i));
                return false;
            }

            return instance.setBlock(blockPos, blockState, i);
        }else {
            return instance.setBlock(blockPos, blockState, i);
        }
    }
}
