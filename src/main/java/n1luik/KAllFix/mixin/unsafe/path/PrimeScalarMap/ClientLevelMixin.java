package n1luik.KAllFix.mixin.unsafe.path.PrimeScalarMap;

import n1luik.KAllFix.Imixin.IEntitySectionStorageAABBMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientLevel.class)
public class ClientLevelMixin {
    @Shadow @Final private TransientEntitySectionManager<Entity> entityStorage;

    @Inject(method = "tickEntities",at = @At("RETURN"))
    private void onTickEntities(CallbackInfo ci) {
        ((IEntitySectionStorageAABBMap<Entity>)entityStorage.sectionStorage).KAllFix$reset();
    }
}