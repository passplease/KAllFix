package n1luik.KAllFix.forge.RenderOptimizer.mixin;

import n1luik.KAllFix.forge.ModInit;
import n1luik.KAllFix.forge.RenderOptimizer.Event;
import n1luik.KAllFix.forge.RenderOptimizer.ui.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModInit.class)
public class This {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(Event.class);
        ModInit.registerKeyBinding(new KeyMapping("k_all_fix.RenderOptimizer.OpenConfigGui", -1, "k_all_fix.name"){
            @Override
            public void setDown(boolean p_90846_) {
                if (p_90846_) {
                    Minecraft.getInstance().setScreen(new ConfigScreen());
                }
                super.setDown(false);
            }
        });
    }
}
