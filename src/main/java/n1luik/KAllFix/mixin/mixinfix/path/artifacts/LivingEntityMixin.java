package n1luik.KAllFix.mixin.mixinfix.path.artifacts;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModItems;
import n1luik.KAllFix.Imixin.mod.artifacts.IArtifactsLivingEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IArtifactsLivingEntityData {
    @Unique
    private boolean isSnowshoes = false;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public boolean KAllFix$isSnowshoes() {
        return isSnowshoes;
    }
    @Override
    public void KAllFix$setSnowshoes(boolean snowshoes) {
        isSnowshoes = snowshoes;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci){
        int offset = (getBlockX() & 0xb111) ^ (getBlockZ() & 0xb111);
        if ((tickCount+offset) % 30 == 0) {
            isSnowshoes = ((WearableArtifactItem)ModItems.SNOWSHOES.get()).isEquippedBy((LivingEntity)(Object)this);
        }
    }
}