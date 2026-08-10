package n1luik.K_multi_threading.neoforge.mixin.debug.ae;

import appeng.init.InitMenuTypes;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Slf4j
@Mixin(value = InitMenuTypes.class)
@Deprecated
public class InitMenuTypesMixin {
    @Inject(method = "queueRegistration", at = @At("RETURN"))
    private static void init(ResourceLocation id, MenuType<?> menuType, CallbackInfo ci) {
        log.info("InitMenuTypesMixin {}", menuType, new Throwable());
    }
}