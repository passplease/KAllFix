package n1luik.K_multi_threading.V1_19.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import n1luik.K_multi_threading.core.util.concurrent.ConcurrentLong2ObjectOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PersistentEntitySectionManager.class, priority = Integer.MAX_VALUE)
public abstract class PersistentEntitySectionManagerFix1 {

    @Shadow public abstract void updateChunkStatus(ChunkPos par1, ChunkHolder.FullChunkStatus par2);

    //@Redirect(method = "<init>", at = @At(value = "NEW", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;<init>()V", ordinal = 1, remap = false))
    //public Long2ObjectOpenHashMap fix1(){
    //    return new ConcurrentLong2ObjectOpenHashMap();
    //}
    @Inject(method = "updateChunkStatus(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/server/level/ChunkHolder$FullChunkStatus;)V", at = @At("HEAD"), cancellable = true)
    public void fix2(ChunkPos p_287590_, ChunkHolder.FullChunkStatus p_287623_, CallbackInfo ci){
        if (Thread.currentThread() == Base.mcs.getRunningThread()){
            if (Base.mcs.overworld().getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig){
                iWorldChunkLockedConfig.execTask(() -> updateChunkStatus(p_287590_, p_287623_));
                ci.cancel();
            }
        }
    }
}
