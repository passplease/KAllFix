package n1luik.K_multi_threading.core.mixin.fix.immersivepetroleum;

import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import n1luik.KAllFix.util.AsyncWait;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ReservoirHandler.class, remap = false)
public class ReservoirHandlerFix1 {
    @Redirect(method = "scanChunkForNewReservoirs", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;m_204166_(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;",remap = false), remap = false)
    private static Holder<Biome> fix1(ServerLevel world, BlockPos pos){
        ServerChunkCache.MainThreadExecutor mainThreadProcessor = world.getChunkSource().mainThreadProcessor;
        if (!mainThreadProcessor.isSameThread()) {
            AsyncWait<Holder<Biome>> aw = new AsyncWait<>(()->world.getBiome(pos));
            mainThreadProcessor.execute(aw);
            aw.waitTask();
            return aw.getReturn_();
        }else {
            return world.getBiome(pos);
        }
    }
}
