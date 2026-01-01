package n1luik.K_multi_threading.core.mixin.fix.eclipticseasons;

import com.teamtea.eclipticseasons.common.AllListener;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.IChunkMap;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AllListener.class)
public abstract class AllListenerFix1 {
    @Shadow(remap = false) public static void onChunkLoad(ChunkEvent.Load event){};

    @Shadow public static void onChunkWatch(ChunkWatchEvent.Watch event){};

    @Inject(method = "onChunkLoad", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fix1(ChunkEvent.Load event, CallbackInfo ci) {
        ChunkSource chunkSource = event.getLevel().getChunkSource();
        if (chunkSource != null && chunkSource instanceof IWorldChunkLockedConfig iw){
            if (iw.isPushThread()){
                Base.getEx().execute(() -> {
                    onChunkLoad(event);
                });
                ci.cancel();
            }
        }
        //else {
        //    Base.LOGGER.info("onChunkLoad null", new Throwable());
        //}
    }
    @Inject(method = "onChunkWatch", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fix2(ChunkWatchEvent.Watch event, CallbackInfo ci) {
        ChunkSource chunkSource = event.getLevel().getChunkSource();
        if (chunkSource != null && chunkSource instanceof ServerChunkCache sc && sc.chunkMap instanceof IChunkMap cm){
            if (cm.KAK$isNot1()){
                Base.getEx().execute(() -> {
                    onChunkWatch(event);
                });
                ci.cancel();
            }
        }
        //else {
        //    Base.LOGGER.info("onChunkLoad null", new Throwable());
        //}
    }
}
