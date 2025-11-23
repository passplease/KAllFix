package n1luik.KAllFix.util.data;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import org.jetbrains.annotations.Nullable;
import org.rocksdb.RocksDBException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public class RocksdbRegionFile extends RegionFile {
    private final RocksdbRoot.RocksDBHandler dbHandler;
    public RocksdbRegionFile(Path p_196950_, Path p_196951_, boolean p_196952_, RocksdbRoot.RocksDBHandler dbHandler) throws IOException {
        super(p_196950_, p_196951_, p_196952_);
        this.dbHandler = dbHandler;
    }

    public RocksdbRegionFile(Path p_63633_, Path p_63634_, RegionFileVersion p_63635_, boolean p_63636_, RocksdbRoot.RocksDBHandler dbHandler) throws IOException {
        super(p_63633_, p_63634_, p_63635_, p_63636_);
        this.dbHandler = dbHandler;
    }

    @Override
    public @Nullable DataInputStream getChunkDataInputStream(ChunkPos p_63646_) throws IOException {
        return KAllFix$getChunkDataInputStream(p_63646_);
    }
    public synchronized @Nullable DataInputStream KAllFix$getChunkDataInputStream(ChunkPos p_63646_) throws IOException {
        try {
            InputStream inputStream = dbHandler.getInputStream(getChunkKey(p_63646_));
            if (inputStream == null) {
                return null;
            }
            return new DataInputStream(inputStream);
        } catch (RocksDBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean doesChunkExist(ChunkPos p_63674_) {
        try {
            return dbHandler.containsString(getChunkKey(p_63674_));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataOutputStream getChunkDataOutputStream(ChunkPos p_63679_) throws IOException {
        return new DataOutputStream(dbHandler.createOutputStream(getChunkKey(p_63679_)));
    }

    @Override
    protected synchronized void write(ChunkPos p_63655_, ByteBuffer p_63656_) throws IOException {
        throw new UnsupportedOperationException("RocksdbRegionFile does not support writing chunks");
    }

    @Override
    public boolean hasChunk(ChunkPos p_63683_) {
        return doesChunkExist(p_63683_);
    }

    public static String getChunkKey(ChunkPos p_63646_) {
        return p_63646_.x + "," + p_63646_.z;
    }
}
