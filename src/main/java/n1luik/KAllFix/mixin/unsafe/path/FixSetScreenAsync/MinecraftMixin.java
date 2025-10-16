package n1luik.KAllFix.mixin.unsafe.path.FixSetScreenAsync;

import n1luik.KAllFix.util.VoidAsyncWait;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(value = Minecraft.class, priority = Integer.MAX_VALUE)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> {
    @Shadow @Final private static Logger LOGGER;

    @Shadow private Thread gameThread;

    public MinecraftMixin(String p_18765_) {
        super(p_18765_);
    }

    @Shadow public abstract void setScreen(@Nullable Screen p_91153_);

    @Shadow protected abstract void updateScreenAndTick(Screen p_91363_);

    @Inject(method = "updateScreenAndTick", at = @At("HEAD"), cancellable = true)
    private void updateScreenAndTick(Screen p_91153_, CallbackInfo ci) {
        if (Thread.currentThread() != gameThread) {
            CompletableFuture.runAsync(() -> {
                updateScreenAndTick(p_91153_);
            }, this).join();
            ci.cancel();
        }
    }
}
