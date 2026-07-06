package n1luik.KAllFix.Imixin;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.AABB;
import org.valkyrienskies.core.impl.shadow.B;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IEntitySectionStorageAABBMap<V extends EntityAccess> {
    void KAllFix$remove(V value);
    void KAllFix$put(V value);
    void KAllFix$reset(V value);
    void KAllFix$get(AABB aabb, List<V> out);
    void KAllFix$get(AABB aabb, Consumer<V> out);
    void KAllFix$get(AABB aabb, Predicate<V> out);
    void KAllFix$reset();
}
