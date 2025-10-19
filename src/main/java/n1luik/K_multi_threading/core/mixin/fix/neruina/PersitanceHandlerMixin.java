package n1luik.K_multi_threading.core.mixin.fix.neruina;

import com.bawnorton.neruina.handler.PersitanceHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PersitanceHandler.class, remap = false)
public abstract class PersitanceHandlerMixin {

    @Shadow(remap = false) private static ServerLevel world;

    @Shadow(remap = false) protected static PersitanceHandler fromNbtInternal(CompoundTag nbt){throw new IllegalStateException("Mixin Shadow Error");};

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static PersitanceHandler getServerState(MinecraftServer server) {
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        world = level;

        assert level != null;

        DimensionDataStorage manager = level.getDataStorage();
        PersitanceHandler handler = (PersitanceHandler)manager.computeIfAbsent(PersitanceHandlerMixin::fromNbtInternal, PersitanceHandler::new, "neruina");
        handler.setDirty();
        return handler;
    }
}
