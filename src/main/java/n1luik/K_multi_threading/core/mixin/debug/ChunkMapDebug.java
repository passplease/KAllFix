package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

@Deprecated
@Mixin(ChunkMap.class)
public class ChunkMapDebug {

    @Shadow private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap;

    @Mutable
    @Shadow @Final private Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap;

    @Mutable
    @Shadow @Final private Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads;

    @Inject(method = "updateChunkScheduling",at = @At("RETURN"))
    public void debug1(long p_140177_, int p_140178_, ChunkHolder p_140179_, int p_140180_, CallbackInfoReturnable<ChunkHolder> cir){
        for (Map.Entry<Long, ChunkHolder> longChunkHolderEntry : updatingChunkMap.entrySet()) {
            Base.LOGGER.info("ChunkMapDebug1 {} {}",longChunkHolderEntry.getKey(),longChunkHolderEntry.getValue().getTicketLevel());
        }
    }

    @Inject(method = "schedule",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/progress/ChunkProgressListener;onStatusChange(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/ChunkStatus;)V"))
    public void debug2(ChunkHolder p_140293_, ChunkStatus p_140294_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
        Base.LOGGER.info("ChunkMapDebug2");
    }

    @Inject(method = "getChunkRangeFuture",at = @At(value = "INVOKE",target = "Ljava/util/function/IntFunction;apply(I)Ljava/lang/Object;",ordinal = 0))
    public void debug3(ChunkHolder p_281446_, int p_282030_, IntFunction<ChunkStatus> p_282923_, CallbackInfoReturnable<CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>>> cir){
        Base.LOGGER.info("ChunkMapDebug3");
    }

    @Inject(method = "getChunkRangeFuture",at = @At(value = "INVOKE",target = "Ljava/util/function/IntFunction;apply(I)Ljava/lang/Object;",ordinal = 1))
    public void debug4(ChunkHolder p_281446_, int p_282030_, IntFunction<ChunkStatus> p_282923_, CallbackInfoReturnable<CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>>> cir){
        Base.LOGGER.info("ChunkMapDebug4");
    }

    @Inject(method = "getChunkRangeFuture",at = @At(value = "INVOKE",target = "Ljava/util/concurrent/CompletableFuture;completedFuture(Ljava/lang/Object;)Ljava/util/concurrent/CompletableFuture;"),locals = LocalCapture.CAPTURE_FAILHARD)
    public void debug5(ChunkHolder p_281446_, int p_282030_, IntFunction<ChunkStatus> p_282923_, CallbackInfoReturnable<CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>>> cir, List list, List list1, ChunkPos chunkpos, int i, int j, int k, int l, int i1, ChunkPos chunkpos1, long j1, ChunkHolder chunkholder){
        Base.LOGGER.info("ChunkMapDebug5 {} {} {}",j1,chunkpos1,p_282030_);
    }
}
