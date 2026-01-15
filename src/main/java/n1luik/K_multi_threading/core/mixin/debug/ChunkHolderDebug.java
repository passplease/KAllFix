package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Deprecated
@Mixin(ChunkHolder.class)
public class ChunkHolderDebug {

    //@Inject(method = "getOrScheduleFuture", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ChunkHolder;updateChunkToSave(Ljava/util/concurrent/CompletableFuture;Ljava/lang/String;)V"))
    //public void debug1(ChunkStatus p_140050_, ChunkMap p_140051_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
    //    Base.LOGGER.info("ChunkHolderDebug.class");
    //}
    @Inject(method = "getOrScheduleFuture", at = @At(value = "RETURN", ordinal = 0))
    public void debug2(ChunkStatus p_140050_, ChunkMap p_140051_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
        Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> now = cir.getReturnValue().getNow(null);
        if (now != null) {
            if (now.left().isPresent()) {
                Base.LOGGER.info("ChunkHolderDebug.class getOrScheduleFuture left: {}", now.left().get());
            }
        }
    }
    @Inject(method = "getOrScheduleFuture", at = @At(value = "RETURN", ordinal = 1))
    public void debug3(ChunkStatus p_140050_, ChunkMap p_140051_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
        Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> now = cir.getReturnValue().getNow(null);
        if (now != null) {
            if (now.left().isPresent()) {
                Base.LOGGER.info("ChunkHolderDebug.class getOrScheduleFuture left2: {}", now.left().get());
            }
        }
    }
    @Inject(method = "getOrScheduleFuture", at = @At(value = "RETURN", ordinal = 2))
    public void debug4(ChunkStatus p_140050_, ChunkMap p_140051_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
        Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> now = cir.getReturnValue().getNow(null);
        if (now != null) {
            if (now.left().isPresent()) {
                Base.LOGGER.info("ChunkHolderDebug.class getOrScheduleFuture left3: {}", now.left().get());
            }
        }
    }
}
