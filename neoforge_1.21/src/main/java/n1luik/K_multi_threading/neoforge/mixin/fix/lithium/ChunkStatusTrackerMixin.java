package n1luik.K_multi_threading.neoforge.mixin.fix.lithium;

import net.caffeinemc.mods.lithium.common.world.chunk.ChunkStatusTracker;
import net.caffeinemc.mods.lithium.mixin.util.accessors.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@Mixin(value = ChunkStatusTracker.class)
public class ChunkStatusTrackerMixin {

    @Shadow @Final private static ArrayList<BiConsumer<ServerLevel, LevelChunk>> LOAD_CALLBACKS;

    @Shadow @Final private static ArrayList<BiConsumer<ServerLevel, ChunkPos>> UNLOAD_CALLBACKS;

    @Overwrite
    public static void onChunkAccessible(ServerLevel serverLevel, LevelChunk levelChunk) {
        if (false){//((LevelAccessor)serverLevel).getThread() != Thread.currentThread()) {
            throw new IllegalStateException("ChunkStatusTracker.onChunkAccessible called on wrong thread!");
        } else {
            for(int i = 0; i < LOAD_CALLBACKS.size(); ++i) {
                ((BiConsumer)LOAD_CALLBACKS.get(i)).accept(serverLevel, levelChunk);
            }

        }
    }
    @Overwrite
    public static void onChunkInaccessible(ServerLevel serverLevel, ChunkPos pos) {
        if (false){//((LevelAccessor)serverLevel).getThread() != Thread.currentThread()) {
            throw new IllegalStateException("ChunkStatusTracker.onChunkInaccessible called on wrong thread!");
        } else {
            for(int i = 0; i < UNLOAD_CALLBACKS.size(); ++i) {
                ((BiConsumer)UNLOAD_CALLBACKS.get(i)).accept(serverLevel, pos);
            }

        }
    }
}