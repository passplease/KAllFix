package n1luik.K_multi_threading.core.mixin.data;

import n1luik.K_multi_threading.core.Imixin.ILevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelChunk.class)
public class LevelChunkMixin implements ILevelChunk {
    @Unique
    private boolean postProcessGeneration = false;
    @Inject(method = "postProcessGeneration", at = @At("RETURN"))
    private void add1(CallbackInfo ci){
        postProcessGeneration = true;
    }

    @Override
    public boolean KMT$postProcessGeneration() {
        return postProcessGeneration;
    }
}