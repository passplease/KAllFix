package n1luik.KAllFix.mixin.mixinfix.path.cataclysm;

import com.github.L_Ender.cataclysm.capabilities.ChargeCapability;
import com.github.L_Ender.cataclysm.capabilities.HookCapability;
import com.github.L_Ender.cataclysm.capabilities.RenderRushCapability;
import n1luik.KAllFix.Imixin.mod.cataclysm.ICataclysmLivingEntityCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ICataclysmLivingEntityCapability {
    @Unique
    private LazyOptional<HookCapability.IHookCapability> KAllFix$cataclysm$HookCapability;
    @Unique
    private LazyOptional<ChargeCapability.IChargeCapability> KAllFix$cataclysm$ChargeCapability;
    @Unique
    private LazyOptional<RenderRushCapability.IRenderRushCapability> KAllFix$cataclysm$RenderRushCapability;

    @Override
    public LazyOptional<HookCapability.IHookCapability> KAllFix$cataclysm$getHookCapability() {
        return KAllFix$cataclysm$HookCapability;
    }

    @Override
    public LazyOptional<ChargeCapability.IChargeCapability> KAllFix$cataclysm$getChargeCapability() {
        return KAllFix$cataclysm$ChargeCapability;
    }

    @Override
    public LazyOptional<RenderRushCapability.IRenderRushCapability> KAllFix$cataclysm$getRenderRushCapability() {
        return KAllFix$cataclysm$RenderRushCapability;
    }

    @Override
    public void KAllFix$cataclysm$setHookCapability(LazyOptional<HookCapability.IHookCapability> data) {
        KAllFix$cataclysm$HookCapability = data;
    }

    @Override
    public void KAllFix$cataclysm$setChargeCapability(LazyOptional<ChargeCapability.IChargeCapability> data) {
        KAllFix$cataclysm$ChargeCapability = data;
    }

    @Override
    public void KAllFix$cataclysm$setRenderRushCapability(LazyOptional<RenderRushCapability.IRenderRushCapability> data) {
        KAllFix$cataclysm$RenderRushCapability = data;
    }
}
