package n1luik.KAllFix.mixin.mixinfix.farm_and_charm;

import n1luik.KAllFix.fix.farm_and_charm.FACOptimizeTag;
import n1luik.KAllFix.util.UtilKAF;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.entity.ai.ChickenLocateCoopGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(value = ChickenLocateCoopGoal.class, remap = false)
public class ChickenLocateCoopGoalMixin {
    @Shadow @Final private Chicken chicken;
    //private boolean debug = true;

    @Redirect(method = "m_8036_", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;m_121930_(Lnet/minecraft/core/BlockPos;IILjava/util/function/Predicate;)Ljava/util/Optional;", remap = false), remap = false)
    private Optional<BlockPos> fix1(BlockPos blockPos, int r, int h, Predicate<BlockPos> predicate) {
        //Level level = this.chicken.level();
        //if (debug) {
        //    debug = true;
        //    BlockState blockState = Blocks.GLASS.defaultBlockState();
        //    return UtilKAF.tagFindClosestMatch(this.chicken.level(), FACOptimizeTag.ChickenCoopBlockTag, blockPos, r, h, v->{
        //        if (predicate.test(v)) {
        //            System.out.println(v);
        //            return true;
        //        }
        //        return false;
        //    });
        //}
//
        return UtilKAF.tagFindClosestMatch(this.chicken.level(), FACOptimizeTag.ChickenCoopBlockTag, blockPos, r, h, predicate);
    }
    //@Redirect(method = "m_8036_", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;m_121930_(Lnet/minecraft/core/BlockPos;IILjava/util/function/Predicate;)Ljava/util/Optional;", remap = false), remap = false)
    //private Optional<BlockPos> debug(BlockPos blockPos, int r, int h, Predicate<BlockPos> predicate) {
    //    Level level = this.chicken.level();
    //    if (debug){
    //        debug = false;
    //        BlockState blockState = Blocks.GLASS.defaultBlockState();
    //        //BlockPos.findClosestMatch(
    //        UtilKAF.tagFindClosestMatch(this.chicken.level(), FACOptimizeTag.ChickenCoopBlockTag,
    //                blockPos, r, h, p -> {
    //            level.setBlock(p, blockState, 3);
    //            return false;
    //        });
    //    }
    //    return BlockPos.findClosestMatch(blockPos, r, h, predicate);
    //}
}
