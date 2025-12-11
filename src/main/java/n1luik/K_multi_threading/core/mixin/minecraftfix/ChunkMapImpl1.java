package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IChunkMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(value = ChunkMap.class, priority = Integer.MAX_VALUE)
public abstract class ChunkMapImpl1 implements IChunkMap {
    @Unique
    private volatile boolean KAK$Unloads = false;
    @Override
    public boolean KAK$Unloads() {
        return KAK$Unloads;
    }

    @Inject(method = "processUnloads", at = @At("HEAD"))
    public void impl1(BooleanSupplier p_140354_, CallbackInfo ci) {
        KAK$Unloads = true;
    }
    @Inject(method = "processUnloads", at = @At("RETURN"))
    public void impl2(BooleanSupplier p_140354_, CallbackInfo ci) {
        KAK$Unloads = false;
    }

}
