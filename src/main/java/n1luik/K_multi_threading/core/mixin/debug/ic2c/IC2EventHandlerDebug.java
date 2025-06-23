package n1luik.K_multi_threading.core.mixin.debug.ic2c;

import ic2.core.IC2;
import ic2.core.platform.events.IC2EventHandler;
import n1luik.K_multi_threading.core.Base;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(IC2EventHandler.class)
public class IC2EventHandlerDebug {
    @Inject(method = "onWorldTickEvent", at = @At("HEAD"), remap = false)
    private void onWorldTickEvent(TickEvent.LevelTickEvent event, CallbackInfo ci) {
        Base.LOGGER.info("IC2EventHandlerDebug.onWorldTickEvent {} {} {} {} {}",
                event.side,
                IC2.NETWORKING.get(),
                EffectiveSide.get(),
                IC2.NETWORKING.get(false),
                IC2.NETWORKING.get(true)
        );
    }
}
