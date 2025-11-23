package n1luik.KAllFix.mixin.unsafe.path.SaveFileCompression;

import n1luik.KAllFix.util.data.RocksdbRegionFile;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

@Mixin(value = RegionFile.class, priority = Integer.MAX_VALUE)
public class RegionFileMixin {
    @Inject(method = "createExternalChunkInputStream", at = @At("HEAD"), cancellable = true)
    public void createExternalChunkInputStream(ChunkPos p_63648_, byte p_63649_, CallbackInfoReturnable<DataInputStream> cir) throws IOException {
        if (((Object)this) instanceof RocksdbRegionFile dbr) {
            cir.setReturnValue(dbr.KAllFix$getChunkDataInputStream(p_63648_));
        }
    }
    @Inject(method = "createChunkInputStream", at = @At("HEAD"), cancellable = true)
    public void createChunkInputStream(ChunkPos p_63651_, byte p_63652_, InputStream p_63653_, CallbackInfoReturnable<DataInputStream> cir) throws IOException {
        if (((Object)this) instanceof RocksdbRegionFile) {
            cir.setReturnValue(new DataInputStream(p_63653_));
        }
    }
}
