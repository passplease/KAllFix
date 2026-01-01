package n1luik.K_multi_threading.core.mixin.impl;

import n1luik.K_multi_threading.core.Imixin.IChunkMap;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

@Mixin(value = ChunkMap.class, priority = Integer.MAX_VALUE)
public abstract class ChunkMapImpl1 implements IChunkMap {
    @Unique
    private volatile boolean KAK$Unloads = false;
    @Unique
    private final AtomicInteger KAK$not1 = new AtomicInteger();
    @Override
    public boolean KAK$Unloads() {
        return KAK$Unloads;
    }

    @Override
    public boolean KAK$isNot1() {
        return KAK$not1.get() > 0;
    }

    @Override
    public void KAK$AddNot1() {
        KAK$not1.incrementAndGet();
    }

    @Override
    public void KAK$RemoveNot1() {
        KAK$not1.decrementAndGet();
    }

    @Inject(method = "processUnloads", at = @At("HEAD"))
    public void impl1(BooleanSupplier p_140354_, CallbackInfo ci) {
        KAK$Unloads = true;
    }
    @Inject(method = "processUnloads", at = @At("RETURN"))
    public void impl2(BooleanSupplier p_140354_, CallbackInfo ci) {
        KAK$Unloads = false;
    }
    @Inject(method = "addEntity", at = @At("HEAD"))
    public void impl3(Entity p_140200_, CallbackInfo ci) {
        KAK$not1.incrementAndGet();
    }
    @Inject(method = "addEntity", at = @At("RETURN"))
    public void impl4(Entity p_140200_, CallbackInfo ci) {
        KAK$not1.decrementAndGet();
    }
    @Inject(method = "playerLoadedChunk", at = @At("HEAD"))
    public void impl5(ServerPlayer p_183761_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183762_, LevelChunk p_183763_, CallbackInfo ci) {
        KAK$not1.incrementAndGet();
    }
    @Inject(method = "playerLoadedChunk", at = @At("RETURN"))
    public void impl6(ServerPlayer p_183761_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183762_, LevelChunk p_183763_, CallbackInfo ci) {
        KAK$not1.decrementAndGet();
    }

}
