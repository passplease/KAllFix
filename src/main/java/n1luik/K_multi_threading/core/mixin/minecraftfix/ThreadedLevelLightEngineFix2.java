package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.KAllFix.util.AsyncWait;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ThreadedLevelLightEngine.class, priority = Integer.MAX_VALUE-1)
public abstract class ThreadedLevelLightEngineFix2 extends LevelLightEngine {
    @Shadow @Final private ChunkMap chunkMap;

    public ThreadedLevelLightEngineFix2(LightChunkGetter p_75805_, boolean p_75806_, boolean p_75807_) {
        super(p_75805_, p_75806_, p_75807_);
    }

    @Redirect(method = "tryScheduleUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/ProcessorMailbox;tell(Ljava/lang/Object;)V"))
    public <T> void fix2(ProcessorMailbox<T> instance, T p_18750_){
        if (chunkMap.level.getChunkSource() instanceof ParaServerChunkProvider paraServerChunkProvider && paraServerChunkProvider.getChunkGeneratorTest() > 0) {
            instance.tell((T)(Runnable)()->{
                paraServerChunkProvider.pushWaitThread();
                ((Runnable)p_18750_).run();
                paraServerChunkProvider.popWait();
            });
        }else {
            instance.tell(p_18750_);
        }

    }
}
