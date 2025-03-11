package n1luik.KAllFix.forge.LoginProtectionMod.mixin;

import n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent;
import net.minecraft.client.gui.screens.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @Inject(method = "init()V", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        LoginProtectionModEvent.LoginProtection = true;
    }
    @Inject(method = "lambda$init$0", at = @At("HEAD"))
    public void impl1(CallbackInfo ci) {
        LoginProtectionModEvent.LoginProtection = false;
    }
}
