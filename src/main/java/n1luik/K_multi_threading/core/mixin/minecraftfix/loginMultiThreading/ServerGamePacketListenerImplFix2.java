package n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplFix2 {
    @Redirect(method = "disconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;executeBlocking(Ljava/lang/Runnable;)V"))
    public void fix1(MinecraftServer instance, Runnable runnable){
        instance.execute(()->{
            instance.executeBlocking(runnable);
        });
    }
}
