package n1luik.KAllFix.mixin.unsafe.path.SaveFileCompression;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import n1luik.KAllFix.util.data.RocksdbRegionFile;
import n1luik.KAllFix.util.data.RocksdbRoot;
import net.minecraft.FileUtil;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;

@Mixin(RegionFileStorage.class)
public class RegionFileStorageMixin {
    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();
    @Shadow @Final private Path folder;
    @Shadow @Final private Long2ObjectLinkedOpenHashMap<RegionFile> regionCache;
    @Shadow @Final private boolean sync;
    @Unique
    private RocksdbRoot.RocksDBHandler dbHandler;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        try {
            Path dbPath = folder.resolve("db.rocksdb");
            LOGGER.info("Initializing RocksDB at: {}", dbPath);
            dbHandler = new RocksdbRoot.RocksDBHandler(dbPath.toFile().getPath());
            dbHandler.setCompressionAlgorithm(RocksdbRoot.CompressionAlgorithm.ZSTD);
            LOGGER.info("RocksDB initialized successfully with ZSTD compression");
        } catch (RocksDBException e) {
            LOGGER.error("Failed to initialize RocksDB", e);
            throw new RuntimeException("Failed to initialize RocksDB", e);
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private RegionFile getRegionFile(ChunkPos p_63712_) throws IOException {
        long i = ChunkPos.asLong(p_63712_.getRegionX(), p_63712_.getRegionZ());
        RegionFile regionfile = this.regionCache.getAndMoveToFirst(i);
        if (regionfile != null) {
            return regionfile;
        } else {
            if (this.regionCache.size() >= 256) {
                this.regionCache.removeLast().close();
            }

            FileUtil.createDirectoriesSafe(this.folder);
            Path path = this.folder.resolve("r." + p_63712_.getRegionX() + "." + p_63712_.getRegionZ() + ".mca");
            RegionFile regionfile1 = new RocksdbRegionFile(path, this.folder, this.sync, dbHandler);
            this.regionCache.putAndMoveToFirst(i, regionfile1);
            return regionfile1;
        }
    }
    @Inject(method = "close", at = @At("HEAD"))
    public void close(CallbackInfo ci) throws IOException {
        LOGGER.info("Closing RocksDB handler");
        dbHandler.close();
        LOGGER.info("RocksDB handler closed successfully");
    }
    @Inject(method = "flush", at = @At("HEAD"))
    public void flush(CallbackInfo ci) throws IOException {
        try {
            dbHandler.flush();
        } catch (RocksDBException e) {
            LOGGER.error("Failed to flush RocksDB", e);
            throw new IOException("Failed to flush RocksDB", e);
        }
    }
}
