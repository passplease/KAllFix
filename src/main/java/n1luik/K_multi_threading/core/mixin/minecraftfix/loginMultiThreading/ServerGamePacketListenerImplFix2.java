package n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplFix2 {
    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "disconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;executeBlocking(Ljava/lang/Runnable;)V"))
    public void fix1(MinecraftServer instance, Runnable runnable){
        instance.execute(()->{
            instance.executeBlocking(runnable);
        });
    }
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;doTick()V"))
    public void fix1(ServerPlayer instance){
        server.execute(instance::doTick);
    }
}
