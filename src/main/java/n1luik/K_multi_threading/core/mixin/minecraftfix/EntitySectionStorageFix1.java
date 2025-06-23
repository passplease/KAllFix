package n1luik.K_multi_threading.core.mixin.minecraftfix;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySectionStorage.class)
public class EntitySectionStorageFix1 {
    @Redirect(method = "getOrCreateSection", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;computeIfAbsent(JLit/unimi/dsi/fastutil/longs/Long2ObjectFunction;)Ljava/lang/Object;", remap = false))
    private <T> T fix1(Long2ObjectMap<T> instance, long key, Long2ObjectFunction<? extends T> mappingFunction) {
        //根据ai
        T value = instance.get(key);
        if (value == null) {
            synchronized (this){
                return instance.computeIfAbsent(key, mappingFunction);
            }
        }
        return value;
    }
}
