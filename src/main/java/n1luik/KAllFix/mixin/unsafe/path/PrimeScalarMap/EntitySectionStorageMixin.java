package n1luik.KAllFix.mixin.unsafe.path.PrimeScalarMap;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import n1luik.KAllFix.Imixin.IEntitySectionStorageAABBMap;
import n1luik.KAllFix.util.PrimeScalarEntitySectionMapMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(value = EntitySectionStorage.class)
public class EntitySectionStorageMixin<T extends EntityAccess> implements IEntitySectionStorageAABBMap<T> {
    @Shadow @Final private Long2ObjectMap<EntitySection<T>> sections;
    @Unique
    private final PrimeScalarEntitySectionMapMap<T> map = new PrimeScalarEntitySectionMapMap<>();

    @Override
    public void KAllFix$remove(T value) {
        map.remove(value);
    }

    @Override
    public void KAllFix$put(T value) {
        map.put(value);
    }

    @Override
    public void KAllFix$reset(T value) {
        map.reset(value);
    }

    @Override
    public void KAllFix$get(AABB aabb, List<T> out) {
        map.get(aabb, out);
    }

    @Override
    public void KAllFix$get(AABB aabb, Consumer<T> out) {
        map.get(aabb, out);
    }
    @Override
    public void KAllFix$get(AABB aabb, Predicate<T> out) {
        map.get(aabb, out);
    }

    @Override
    public void KAllFix$reset() {
        // 收集所有实体到列表
        List<T> allEntities = new ArrayList<>(1000);
        for (EntitySection<T> section : sections.values()) {
            section.getEntities().forEach(allEntities::add);
        }
        // 使用批量插入方法，避免逐个put的分裂开销
        map.clearAndAddAll(allEntities);
    }
}