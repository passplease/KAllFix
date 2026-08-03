package n1luik.K_multi_threading.neoforge.mixin.impl;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import n1luik.KAllFix.util.ChunkStatusSwap;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(value = ParaServerChunkProvider.class)
public abstract class ParaServerChunkProviderMixin extends ServerChunkCache {

    @Shadow public abstract ChunkAccess lookupChunkFull(long chunkPos);

    public ParaServerChunkProviderMixin(ServerLevel level, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer fixerUpper, StructureTemplateManager structureManager, Executor dispatcher, ChunkGenerator generator, int viewDistance, int simulationDistance, boolean sync, ChunkProgressListener progressListener, ChunkStatusUpdateListener chunkStatusListener, Supplier<DimensionDataStorage> overworldDataStorage) {
        super(level, levelStorageAccess, fixerUpper, structureManager, dispatcher, generator, viewDistance, simulationDistance, sync, progressListener, chunkStatusListener, overworldDataStorage);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Nullable
    public LevelChunk getChunkNow(int chunkX, int chunkZ) {
        long i = ChunkPos.asLong(chunkX, chunkZ);

        ChunkAccess c = lookupChunkFull(i);
        if (c != null) {
            return (LevelChunk) c;
        }
        for (int j = 0; j < 4; j++) {
            if (i == this.lastChunkPos[j] && this.lastChunkStatus[j] == ChunkStatus.FULL) {
                ChunkAccess chunkaccess = this.lastChunk[j];
                return chunkaccess instanceof LevelChunk ? (LevelChunk)chunkaccess : null;
            }
        }

        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(i);
        if (chunkholder == null) {
            return null;
        } else {
            if (chunkholder.currentlyLoading != null) return chunkholder.currentlyLoading; // Forge: If the requested chunk is loading, bypass the future chain to prevent a deadlock.
            ChunkAccess chunkaccess1 = chunkholder.getChunkIfPresent(ChunkStatus.FULL);
            if (chunkaccess1 != null) {
                this.storeInCache(i, chunkaccess1, ChunkStatus.FULL);
                if (chunkaccess1 instanceof LevelChunk) {
                    return (LevelChunk)chunkaccess1;
                }
            }

            return null;
        }

        /*//log.debug("Missed chunk " + i + " now");
        //synchronized (this){
            LevelChunk cl = super.getChunkNow(chunkX, chunkZ);
            cacheChunk(i, cl, ChunkStatus.FULL);
            return cl;
        //}*/
    }

    @Overwrite
    private ChunkAccess readChunk(Object o){
        ChunkAccess chunkAccess = readChunkNull(o);
        assert chunkAccess != null;
        return chunkAccess;
    }
    @Overwrite
    private ChunkAccess readChunkNull(Object o){
        return ((ChunkResult<ChunkAccess>)o).orElse(null);
    }
    @Overwrite
    private CompletableFuture genTask(ChunkAccess o){
        return CompletableFuture.completedFuture(ChunkResult.of(o));
    }

}