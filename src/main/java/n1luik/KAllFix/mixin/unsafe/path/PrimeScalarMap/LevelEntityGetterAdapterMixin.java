package n1luik.KAllFix.mixin.unsafe.path.PrimeScalarMap;

import n1luik.KAllFix.Imixin.IEntitySectionStorageAABBMap;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.*;

import java.util.function.Consumer;

@Mixin(value = LevelEntityGetterAdapter.class)
public class LevelEntityGetterAdapterMixin<T extends EntityAccess> {

    @Shadow @Final private EntitySectionStorage<T> sectionStorage;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void get(AABB p_156956_, Consumer<T> p_156957_) {
        ((IEntitySectionStorageAABBMap<T>)this.sectionStorage).KAllFix$get(p_156956_, p_156957_);//.getEntities(p_156956_, AbortableIterationConsumer.forConsumer(p_156957_));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public <U extends T> void get(EntityTypeTest<T, U> p_261696_, AABB p_261693_, AbortableIterationConsumer<U> p_261719_) {
        ((IEntitySectionStorageAABBMap<T>)this.sectionStorage).KAllFix$get(p_261693_, v->{
            U p261708 = p_261696_.tryCast(v);
            if (p261708 == null) return false;
            return p_261719_.accept(p261708).shouldAbort();
        });//this.sectionStorage.getEntities(p_261696_, p_261693_, p_261719_);
    }
}