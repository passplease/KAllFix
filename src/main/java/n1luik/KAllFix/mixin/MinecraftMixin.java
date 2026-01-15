package n1luik.KAllFix.mixin;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "emergencySave", at = @At("HEAD"))
    private void runTick(CallbackInfo ci) {
        int i = GL11.glGetError();
        if (i != GL11.GL_NO_ERROR) {
            Base.LOGGER.error("emergencySave GL error: {}", i);
        }
    }

}