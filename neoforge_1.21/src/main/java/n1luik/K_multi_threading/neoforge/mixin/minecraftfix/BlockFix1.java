package n1luik.K_multi_threading.neoforge.mixin.minecraftfix;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(value = Block.class)
public class BlockFix1 {
    private static final ThreadLocal<List<ItemEntity>> k_multi_threading$capturedDropsMT = new ThreadLocal<>();

    @Overwrite
    /**
     * Ends the drop capture process by setting {@link #capturedDrops} to null and returning the old list.
     * <p>
     * Must only be called on the server thread.
     */
    private static List<ItemEntity> stopCapturingDrops() {
        List<ItemEntity> drops = k_multi_threading$capturedDropsMT.get();
        k_multi_threading$capturedDropsMT.set(null);
        return drops;
    }
    @Overwrite
    /**
     * Initializes {@link #capturedDrops}, starting the drop capture process.
     * <p>
     * Must only be called on the server thread.
     */
    private static void beginCapturingDrops() {
        k_multi_threading$capturedDropsMT.set(new java.util.ArrayList<>());
    }


}