package n1luik.KAllFix.mixin.naturesaura;

import de.ellpeck.naturesaura.events.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(value = ClientEvents.class, remap = false)
public class ClientEventsMixin {
    @Inject(method = "onClientTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void KAllFix$fix1(TickEvent.ClientTickEvent event, CallbackInfo ci){
        if (event.phase != TickEvent.Phase.END) {
            Minecraft instance = Minecraft.getInstance();
            if (instance.player == null || instance.level == null) {
                ci.cancel();
            }
        }
    }
}
