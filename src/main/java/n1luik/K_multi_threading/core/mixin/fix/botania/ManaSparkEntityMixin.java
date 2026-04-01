package n1luik.K_multi_threading.core.mixin.fix.botania;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.impl.shadow.E;
import vazkii.botania.common.entity.ManaSparkEntity;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = ManaSparkEntity.class, remap = false)
public class ManaSparkEntityMixin {
    @Redirect(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At(value = "INVOKE", target = "Ljava/util/Collections;newSetFromMap(Ljava/util/Map;)Ljava/util/Set;", remap = false), remap = false)
    public Set<E> fix1(Map<E, Boolean> map){
        return ConcurrentHashMap.newKeySet();
    }
}