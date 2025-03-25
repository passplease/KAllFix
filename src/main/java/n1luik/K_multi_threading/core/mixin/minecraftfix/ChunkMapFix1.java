package n1luik.K_multi_threading.core.mixin.minecraftfix;

import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(ChunkMap.class)
public class ChunkMapFix1 {
    @Shadow @Final private ServerLevel level;

    //@Redirect(method = "lambda$prepareTickingChunk$41", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;postProcessGeneration()V"))
    //public void fix1(LevelChunk instance){
    //    if(level.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
    //        iWorldChunkLockedConfig.execWaitTask(instance::postProcessGeneration);
    //    }else {
    //        instance.postProcessGeneration();
    //    }
    //}

    @Redirect(method = "prepareTickingChunk", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenApplyAsync(Ljava/util/function/Function;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", ordinal = 1))
    public <T extends Either<LevelChunk, ChunkHolder. ChunkLoadingFailure>> CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>
    fix1(CompletableFuture<T> instance, Function<? super T, Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> fn, Executor executor){
        if(level.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            return instance.thenApplyAsync(v->
                        v.ifLeft((p_214960_) -> {
                            iWorldChunkLockedConfig.execWaitTask(p_214960_::postProcessGeneration);
                            this.level.startTickingChunk(p_214960_);
                        })
            , executor);

        }else {
            return instance.thenApplyAsync(fn, executor);
        }
    }
}
