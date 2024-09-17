package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkMap.class)
public class ChunkMapFix1 {
    @Shadow @Final private ServerLevel level;

    @Redirect(method = "lambda$prepareTickingChunk$41", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;postProcessGeneration()V"))
    public void fix1(LevelChunk instance){
        if(level.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            iWorldChunkLockedConfig.execWaitTask(instance::postProcessGeneration);
        }else {
            instance.postProcessGeneration();
        }
    }
}
