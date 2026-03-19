package n1luik.KAllFix.mixin.mixinfix.path.artifacts;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.item.wearable.feet.SnowshoesItem;
import n1luik.KAllFix.Imixin.mod.artifacts.IArtifactsLivingEntityData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SnowshoesItem.class)
public class SnowshoesItemMixin {
    @Redirect(method = "getModifiedFriction", at = @At(value = "INVOKE", target = "Lartifacts/item/wearable/WearableArtifactItem;isEquippedBy(Lnet/minecraft/world/entity/LivingEntity;)Z", remap = false), remap = false)
    private static boolean isEquippedBy(WearableArtifactItem instance, LivingEntity entity) {
        return ((IArtifactsLivingEntityData)entity).KAllFix$isSnowshoes();
    }

}