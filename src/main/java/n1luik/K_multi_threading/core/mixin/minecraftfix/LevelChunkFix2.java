package n1luik.K_multi_threading.core.mixin.minecraftfix;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.impl.shadow.V;

@Mixin(LevelChunk.class)
public abstract class LevelChunkFix2 {

    @Redirect(method = "getListenerRegistry",at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;computeIfAbsent(ILit/unimi/dsi/fastutil/ints/Int2ObjectFunction;)Ljava/lang/Object;"))
    private <V> V fix1(Int2ObjectMap<V> instance, int key, Int2ObjectFunction<? extends V> mappingFunction) {
        synchronized (instance){
            return instance.computeIfAbsent(key, mappingFunction);
        }
    }
}
