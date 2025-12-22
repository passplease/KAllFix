package n1luik.K_multi_threading.core.mixin.fix.mowziesmobs;

import com.bobmowzie.mowziesmobs.server.bossinfo.MMBossInfoServer;
import com.bobmowzie.mowziesmobs.server.entity.MowzieEntity;
import n1luik.K_multi_threading.core.Imixin.IChunkMap;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MMBossInfoServer.class,remap = false)
public abstract class MMBossInfoServerFix1 extends ServerBossEvent {
    @Shadow(remap = false) @Final protected MowzieEntity entity;

    @Shadow(remap = false) public abstract void m_6543_(ServerPlayer player);

    public MMBossInfoServerFix1(Component p_8300_, BossBarColor p_8301_, BossBarOverlay p_8302_) {
        super(p_8300_, p_8301_, p_8302_);
    }

    @Inject(method = "m_6543_", at = @At("HEAD"), cancellable = true, remap = false)
    private void fixUpdateBossInfo(ServerPlayer player, CallbackInfo ci) {
        if (entity != null) {
            var level = entity.level();
            if (level != null) {
                var cs = level.getChunkSource();
                if (cs instanceof ParaServerChunkProvider ps && ParaServerChunkProvider.generatorAllThread != Thread.currentThread()){
                    if (ps.chunkMap instanceof IChunkMap cm){
                        if (cm.KAK$isNot1() || Thread.currentThread() == level.getServer().getRunningThread()) {
                            ci.cancel();
                            ps.KMT$addTickRun(()->m_6543_(player));
                        }
                    }
                }
            }
        }
    }
}
