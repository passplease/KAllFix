package n1luik.K_multi_threading.neoforge.mixin.fix.sable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableCommonEvents;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LevelChunk.class})
public class LevelChunkMixin {
    @Shadow
    @Final
    private Level level;
    //@Unique
    //private BlockPos sable$blockSet = null;

    //@Inject(
    //    method = {"setBlockState"},
    //    at = {@At("HEAD")}
    //)
    //private void sable$preSetBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir) {
    //    this.sable$blockSet = pPos;
    //}

    @Inject(
        method = {"setBlockState"},
        at = {@At("RETURN")}
    )
    private void sable$postSetBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir) {
        BlockPos sable$blockSet1 = pPos;//this.sable$blockSet;
        if (sable$blockSet1 != null) {
            SubLevel subLevel = Sable.HELPER.getContaining(this.level, sable$blockSet1);
            if (subLevel != null) {
                subLevel.getPlot().onBlockChange(sable$blockSet1, pState);
            }
        }

        //this.sable$blockSet = null;
    }

    @WrapOperation(
        method = {"setBlockState"},
        at = {@At(
    value = "INVOKE",
    target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"
)}
    )
    private BlockState sable$setBlockState(LevelChunkSection instance, int pX, int pY, int pZ, BlockState newState, Operation<BlockState> original, @Local(index = 1) BlockPos sable$blockSet1) {
        BlockState oldState = (BlockState)original.call(instance, pX, pY, pZ, newState);
        Level var9 = this.level;
        //BlockPos sable$blockSet1 = this.sable$blockSet;
        if (var9 instanceof ServerLevel serverLevel) {
            if (oldState != newState) {
                pX = sable$blockSet1.getX();
                pY = sable$blockSet1.getY();
                pZ = sable$blockSet1.getZ();
                SableCommonEvents.handleBlockChange(serverLevel, (LevelChunk)(Object)this, pX, pY, pZ, oldState, newState);
            }
        }

        return oldState;
    }
}
