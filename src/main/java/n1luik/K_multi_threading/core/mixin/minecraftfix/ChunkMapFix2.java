package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Mixin(ChunkMap.class)
public abstract class ChunkMapFix2 {


    @Shadow protected abstract void processUnloads(BooleanSupplier p_140354_);

    @Shadow @Final public ServerLevel level;

    @Redirect(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;processUnloads(Ljava/util/function/BooleanSupplier;)V"))
    public void fix5(ChunkMap instance, BooleanSupplier chunkholder){
        if (level.getChunkSource() instanceof IWorldChunkLockedConfig paraServerChunkProvider) {
            paraServerChunkProvider.KMT$genTestTickRun(()->processUnloads(chunkholder));
        }else {
            processUnloads(chunkholder);
        }

    }
}
