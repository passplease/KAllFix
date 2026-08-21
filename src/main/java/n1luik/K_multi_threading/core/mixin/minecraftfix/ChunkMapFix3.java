package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Mixin(ChunkMap.class)
public abstract class ChunkMapFix3 {



    @Shadow protected abstract boolean saveChunkIfNeeded(ChunkHolder p_198875_);

    @Redirect(method = "processUnloads", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;saveChunkIfNeeded(Lnet/minecraft/server/level/ChunkHolder;)Z"))
    public boolean fix5(ChunkMap instance, ChunkHolder i){
        if (i == null)return false;

        return saveChunkIfNeeded(i);
    }
}
