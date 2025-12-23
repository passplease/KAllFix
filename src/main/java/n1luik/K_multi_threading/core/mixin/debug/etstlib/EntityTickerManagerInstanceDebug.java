package n1luik.K_multi_threading.core.mixin.debug.etstlib;

import com.c2h6s.etstlib.content.misc.entityTicker.EntityTickerManager;
import com.c2h6s.etstlib.content.misc.entityTicker.tickers.Freezing;
import lombok.extern.slf4j.Slf4j;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Slf4j
@Mixin(value = EntityTickerManager.EntityTickerManagerInstance.class, remap = false)
public class EntityTickerManagerInstanceDebug {
    @Inject(method = "setTicker", at = @At("RETURN"), remap = false)
    private void debug(CallbackInfo ci) {
        log.info("setTicker", new Throwable());
    }
}
