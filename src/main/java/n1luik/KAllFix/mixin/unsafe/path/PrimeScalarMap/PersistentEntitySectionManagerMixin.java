package n1luik.KAllFix.mixin.unsafe.path.PrimeScalarMap;

import n1luik.KAllFix.Imixin.IEntitySectionStorageAABBMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin<T extends EntityAccess>{
    @Shadow @Final private EntitySectionStorage<T> sectionStorage;

    @Inject(method = "addEntityWithoutEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntitySection;add(Lnet/minecraft/world/level/entity/EntityAccess;)V"))
    private void add(T p_157539_, boolean p_157540_, CallbackInfoReturnable<Boolean> cir) {
        ((IEntitySectionStorageAABBMap<T>)sectionStorage).KAllFix$put(p_157539_);
    }
}