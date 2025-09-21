package n1luik.KAllFix.mixin.mixinfix.path.upgradedadditionaltrims;

import n1luik.KAllFix.Imixin.mod.upgradedadditionaltrims.Iupgradedadditionaltrims;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements Iupgradedadditionaltrims {
    @Unique
    private byte KAllFix$upgradedadditionaltrims$arg;
    @Override
    public final void KAllFix$upgradedadditionaltrims$setArg(byte arg) {
        this.KAllFix$upgradedadditionaltrims$arg = arg;
    }

    @Override
    public final byte KAllFix$upgradedadditionaltrims$getArg() {
        return KAllFix$upgradedadditionaltrims$arg;
    }
}
