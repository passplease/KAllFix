package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Deprecated
@Mixin(ServerChunkCache.class)
public class ServerChunkCacheDebug {

    @Inject(method = "getChunk", at = @At("HEAD"))
    public void debug1(int p_8360_, int p_8361_, ChunkStatus p_8362_, boolean p_8363_, CallbackInfoReturnable<ChunkAccess> cir){
        Base.LOGGER.info("ServerChunkCache.class {} {} {}",p_8360_,p_8361_, ChunkLevel.byStatus(p_8362_));
    }

    @Inject(method = "getChunkFutureMainThread", at = @At("RETURN"),locals = LocalCapture.CAPTURE_FAILHARD)
    public void debug2(int p_8457_, int p_8458_, ChunkStatus p_8459_, boolean p_8460_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir, ChunkPos chunkpos, long i, int j, ChunkHolder chunkholder){
        Base.LOGGER.info("ServerChunkCache.class2 {} {} {}",chunkholder == null, chunkholder != null && chunkholder.getTicketLevel() > j,j);
    }
}
