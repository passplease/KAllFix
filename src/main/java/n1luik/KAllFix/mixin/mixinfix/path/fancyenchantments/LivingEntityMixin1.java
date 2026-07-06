package n1luik.KAllFix.mixin.mixinfix.path.fancyenchantments;

import n1luik.KAllFix.Imixin.IfancyenchantmentsLivingEntity;
import n1luik.KAllFix.data.fancyenchantments.UtilData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin1 implements IfancyenchantmentsLivingEntity {
    @Unique
    private int KAllFix$fancyenchantments_up_tag = UtilData.startSize;

    @Override
    public int KAllFix$fancyenchantments_up_tag() {
        return KAllFix$fancyenchantments_up_tag;
    }

    @Override
    public void KAllFix$fancyenchantments_up_tag(int i) {
        KAllFix$fancyenchantments_up_tag = i;
    }
//    static {
//        UtilData.fancyenchantments_up_tag = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
//    }
//
//    @Inject(method = "defineSynchedData", at = @At("RETURN"))
//    public void addStart(CallbackInfo ci){
//        entityData.define(UtilData.fancyenchantments_up_tag, UtilData.startSize);
//
//    }
    @Inject(method = "verifyEquippedItem", at = @At("HEAD"))
    public void addTag(ItemStack p_181123_, CallbackInfo ci){
        KAllFix$fancyenchantments_up_tag = UtilData.startSize;
        //getEntityData().set(UtilData.fancyenchantments_up_tag, UtilData.startSize);
    }
}
