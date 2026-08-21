package n1luik.K_multi_threading.neoforge.mixin.fix.sable;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(value = ServerSubLevelContainer.class)
public abstract class ServerSubLevelContainerMixin extends SubLevelContainer {

    @Shadow public abstract ServerLevel getLevel();

    public ServerSubLevelContainerMixin(Level level, int logSideLength, int logPlotSize, int originX, int originZ) {
        super(level, logSideLength, logPlotSize, originX, originZ);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void fix1(CallbackInfo ci) {
        MinecraftServer server = getLevel().getServer();
        if (server.getRunningThread() != Thread.currentThread()) {
            ci.cancel();
            server.execute(this::tick);
        }
    }
}