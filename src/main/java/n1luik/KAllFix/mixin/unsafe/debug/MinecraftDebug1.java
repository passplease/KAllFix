package n1luik.KAllFix.mixin.unsafe.debug;

import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = Integer.MAX_VALUE)
public class MinecraftDebug1 {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void setScreen(CallbackInfo ci) {
        LOGGER.info("setScreen {}", Thread.currentThread(), new Throwable());
    }
}
