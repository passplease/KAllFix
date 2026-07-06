package n1luik.KAllFix.mixin.unsafe.path.PrimeScalarMap;

import n1luik.KAllFix.Imixin.IEntitySectionStorageAABBMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLevel.class)
public class ServerLevelMixin {

    @Shadow @Final private PersistentEntitySectionManager<Entity> entityManager;

    @Inject(method = "tick",at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        ((IEntitySectionStorageAABBMap<Entity>)this.entityManager.sectionStorage).KAllFix$reset();
    }
}