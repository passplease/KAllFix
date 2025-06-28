package n1luik.K_multi_threading.core.mixin.impl;

import n1luik.K_multi_threading.core.Imixin.IChunkStatusLocator;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

//数据结构：
// 1~2(16bit)：深度
// 8(8bit)：独立隔离id
@Deprecated(since="太麻烦了，需要再搞")
@Mixin(ChunkStatus.class)
public abstract class ChunkStatusImpl1 implements IChunkStatusLocator {
   // @Shadow @Final private ChunkStatus parent;
   // @Unique
   // private long level = K_multi_threading$init();
   // public int K_multi_threading$init() {
   //     if (parent == null)
   //         return I;
   //     return 0;
   // }
   //
   // @Override
   // public synchronized long K_multi_threading$up(ChunkStatus cs, int level) {
   //     return 0;
   // }
//
   // @Override
   // public int K_multi_threading$getLevel() {
   //     return level;
   // }
}
