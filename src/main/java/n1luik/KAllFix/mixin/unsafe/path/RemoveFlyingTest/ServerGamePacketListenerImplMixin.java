package n1luik.KAllFix.mixin.unsafe.path.RemoveFlyingTest;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow private int aboveGroundTickCount;

    @Shadow private int aboveGroundVehicleTickCount;

    @Inject(method = "tick", at = @At("HEAD"))
    public void impl1(CallbackInfo ci){
        aboveGroundTickCount = 0;
        aboveGroundVehicleTickCount = 0;
    }
}
