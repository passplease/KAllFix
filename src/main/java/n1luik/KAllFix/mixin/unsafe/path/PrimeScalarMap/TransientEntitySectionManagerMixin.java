package n1luik.KAllFix.mixin.unsafe.path.PrimeScalarMap;

import n1luik.KAllFix.Imixin.IEntitySectionStorageAABBMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TransientEntitySectionManager.class)
public class TransientEntitySectionManagerMixin<T extends EntityAccess> {

    @Shadow @Final private EntitySectionStorage<T> sectionStorage;

    @Inject(method = "addEntity", at = @At("RETURN"))
    private void onAddEntity(T entity, CallbackInfo ci) {
        ((IEntitySectionStorageAABBMap<T>)this.sectionStorage).KAllFix$put(entity);
    }
}