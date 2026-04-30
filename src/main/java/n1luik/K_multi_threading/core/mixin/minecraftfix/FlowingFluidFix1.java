package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.KAllFix.util.AsyncWait;
import n1luik.KAllFix.util.VoidAsyncWait;
import n1luik.K_multi_threading.core.Imixin.ILevelChunk;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FlowingFluid.class, priority = 900)
public class FlowingFluidFix1 {
    @Redirect(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState fix1(Level instance, BlockPos pos){
        ChunkSource chunkSource = instance.getChunkSource();
        //if (chunkSource instanceof IWorldChunkLockedConfig iw){
        if (chunkSource instanceof ParaServerChunkProvider psc){
            //ChunkAccess chunk = chunkSource.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.EMPTY, true);
            ChunkAccess chunk = psc.getBufMax(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));//getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.EMPTY, true);
            if (chunk instanceof LevelChunk il && il.getStatus().getIndex() >= ChunkStatus.FEATURES.getIndex()) {
                //if (il.KMT$postProcessGeneration()){
                    return chunk.getBlockState(pos);
                //}else {
                //    AsyncWait<BlockState> asyncWait = new AsyncWait<>(() -> instance.getBlockState(pos));
                //    iw.KMTIMainThreadExecutor().execute(asyncWait);
                //    asyncWait.waitTask();
                //    return asyncWait.getReturn_();
                //}
            }
        }
        return instance.getBlockState(pos);
    }
}