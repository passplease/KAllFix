package n1luik.K_multi_threading.neoforge.mixin.fix.modern_industrialization;

import aztech.modern_industrialization.machines.multiblocks.world.ChunkEventListeners;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChunkEventListeners.class)
public class ChunkEventListenersMixin {
    @Redirect(method = "ensureServerThread", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isSameThread()Z"))
    private static boolean isSameThread(MinecraftServer minecraftServer){
        return true;
    }
}