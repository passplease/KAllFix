package n1luik.KAllFix.Imixin.mod.cataclysm;

import com.github.L_Ender.cataclysm.capabilities.ChargeCapability;
import com.github.L_Ender.cataclysm.capabilities.HookCapability;
import com.github.L_Ender.cataclysm.capabilities.RenderRushCapability;
import net.minecraftforge.common.util.LazyOptional;

public interface ICataclysmLivingEntityCapability {
    LazyOptional<HookCapability.IHookCapability> KAllFix$cataclysm$getHookCapability();
    LazyOptional<ChargeCapability.IChargeCapability> KAllFix$cataclysm$getChargeCapability();
    LazyOptional<RenderRushCapability.IRenderRushCapability> KAllFix$cataclysm$getRenderRushCapability();
    void KAllFix$cataclysm$setHookCapability(LazyOptional<HookCapability.IHookCapability> data);
    void KAllFix$cataclysm$setChargeCapability(LazyOptional<ChargeCapability.IChargeCapability> data);
    void KAllFix$cataclysm$setRenderRushCapability(LazyOptional<RenderRushCapability.IRenderRushCapability> data);
}
