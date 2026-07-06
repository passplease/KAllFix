package n1luik.KAllFix.mixin.unsafe.path.PrimeScalarMap;

import n1luik.KAllFix.Imixin.IEntitySectionStorageAABBMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.level.entity.TransientEntitySectionManager$Callback")
public class TransientEntitySectionManager_CallbackMixin<T extends EntityAccess> {
    @Shadow @Final private TransientEntitySectionManager this$0;

    @Shadow @Final private T entity;

    @Inject(method = "onRemove", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityLookup;remove(Lnet/minecraft/world/level/entity/EntityAccess;)V"))
    private void onRemove(Entity.RemovalReason p_157678_, CallbackInfo ci) {
        ((IEntitySectionStorageAABBMap<T>)this$0.sectionStorage).KAllFix$remove(entity);
    }
}