package n1luik.K_multi_threading.core.mixin.fix.rats;

import com.github.alexthe666.rats.server.entity.rat.DiggingRat;
import com.github.alexthe666.rats.server.entity.rat.Rat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Rat.class, remap = false)
public abstract class RatFix1 extends DiggingRat {
    protected RatFix1(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    @Inject(method = "m_269323_", at = @At("HEAD"), cancellable = true, remap = false)
    private void fix1(CallbackInfoReturnable<LivingEntity> cir) {
        if (getOwnerUUID() == null || level() == null) {
            cir.setReturnValue(null);
        }
    }

}
