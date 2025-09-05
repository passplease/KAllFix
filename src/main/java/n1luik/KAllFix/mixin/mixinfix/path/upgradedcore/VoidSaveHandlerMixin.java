package n1luik.KAllFix.mixin.mixinfix.path.upgradedcore;

import com.rolfmao.upgradedcore.handlers.VoidSaveHandler;
import com.rolfmao.upgradedcore.utils.TeleportUtil;
import n1luik.KAllFix.data.upgradedcore.TeleportUtil2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.rolfmao.upgradedcore.handlers.VoidSaveHandler.getLastSafePos;

@Mixin(value = VoidSaveHandler.class, remap = false)
public abstract class VoidSaveHandlerMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static BlockPos getLastSafePos(LivingEntity livingEntity) {
        CompoundTag persistentData = livingEntity.getPersistentData();
        int[] upgradedCoreLastPos = persistentData.getIntArray("upgraded_core_last_pos");
        return persistentData.contains("upgraded_core_last_pos") ?
                new BlockPos(upgradedCoreLastPos[0],
                        upgradedCoreLastPos[1], upgradedCoreLastPos[2]) : null;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static void setLastSafePos(LivingEntity livingEntity, BlockPos blockPos) {
        CompoundTag persistentData = livingEntity.getPersistentData();
        boolean r;
        if (!persistentData.contains("upgraded_core_last_pos")){
            r = true;
        }else {
            int[] upgradedCoreLastPos = persistentData.getIntArray("upgraded_core_last_pos");
            BlockPos blockPos1 = livingEntity.blockPosition();
            r = blockPos1.getX() != upgradedCoreLastPos[0] || blockPos1.getY() != upgradedCoreLastPos[1] || blockPos1.getZ() != upgradedCoreLastPos[2];
        }
        if (r){
            persistentData.putIntArray("upgraded_core_last_pos", new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
        }

    }

    //@Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    /**
     * @author
     * @reason
     */
    @SubscribeEvent
    @Overwrite(remap = false)
    public void onLivingUpdate(LivingEvent.LivingTickEvent event){//, CallbackInfo ci) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        BlockPos blockpos = livingEntity.blockPosition().below();
        if (livingEntity.onGround()) {
            LevelChunk chunkNow = level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(blockpos.getX()), SectionPos.blockToSectionCoord(blockpos.getZ()));
            if (chunkNow == null) {
                return;
            }
            BlockState blockstate = chunkNow.getBlockState(blockpos);
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(blockpos.getX(), blockpos.getY()+1, blockpos.getZ());
            if (blockstate.blocksMotion() && TeleportUtil2.isSafeSpotR(level, chunkNow, blockPos)) {
                setLastSafePos(livingEntity, blockPos);
            }
        } else if (livingEntity instanceof Player player) {
            //ci.cancel();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            if (!player.onGround() && !player.getAbilities().instabuild && player.tickCount % 200 == 0) {
                LevelChunk chunkNow = level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(blockpos.getX()), SectionPos.blockToSectionCoord(blockpos.getZ()));
                if (chunkNow == null) {
                    return;
                }
                for(int i = blockpos.getY(); i > level.getMinBuildHeight(); --i) {
                    mutableBlockPos.set(blockpos.getX(), i, blockpos.getZ());
                    BlockState blockstate = chunkNow.getBlockState(mutableBlockPos);
                    if (blockstate.blocksMotion()) {
                        mutableBlockPos.set(blockpos.getX(), i + 1, blockpos.getZ());
                        if (TeleportUtil2.isSafeSpotR(level, chunkNow, mutableBlockPos)) {
                            setLastSafePos(player, mutableBlockPos);
                        }
                        break;
                    }
                }
            }
        }
    }
}
