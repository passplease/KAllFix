package n1luik.KAllFix.mixin.mixinfix.path.upgradedadditionaltrims;

import com.google.common.collect.Multimap;
import com.rolfmao.upgradedadditionaltrims.handlers.EffectTrimsEvent;
import com.rolfmao.upgradedadditionaltrims.utils.AdditionalTrimsUtil;
import n1luik.KAllFix.Imixin.upgradedadditionaltrims.Iupgradedadditionaltrims;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EffectTrimsEvent.class, remap = false)
public abstract class EffectTrimsEventMixin {
    @Shadow protected abstract Multimap<Attribute, AttributeModifier> SwimAttributeMap(Integer mult);

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lcom/rolfmao/upgradedadditionaltrims/utils/AdditionalTrimsUtil;countPrismarineTrim(Lnet/minecraft/world/entity/LivingEntity;)I", ordinal = 0), remap = false)
    public int fix2(LivingEntity livingEntity) {
        Iupgradedadditionaltrims livingEntity1 = (Iupgradedadditionaltrims) livingEntity;
        boolean b = livingEntity1.KAllFix$upgradedadditionaltrims$getArg();
        if (AdditionalTrimsUtil.countPrismarineTrim(livingEntity) > 0) {
            if (!b) {
                livingEntity.getAttributes().addTransientAttributeModifiers(this.SwimAttributeMap(AdditionalTrimsUtil.countPrismarineTrim(livingEntity)));
                livingEntity1.KAllFix$upgradedadditionaltrims$setArg(true);
                return 0;
            }
        } else {
            if (!b) {
                return 0;
            }
            livingEntity.getAttributes().removeAttributeModifiers(this.SwimAttributeMap(0));
        }
        return 0;
    }
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true, remap = false)
    private void fix1(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        Iterable<ItemStack> wearedList = event.getEntity().getArmorSlots();
        if (!wearedList.iterator().hasNext()) {
            ci.cancel();
        }

    }
}
