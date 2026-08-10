package n1luik.K_multi_threading.neoforge.mixin.fix.fastbench;

import dev.shadowsoffire.fastbench.FastBench;
import dev.shadowsoffire.fastbench.util.CraftingInventoryExt;
import dev.shadowsoffire.fastbench.util.FastBenchUtil;
import dev.shadowsoffire.fastbench.util.SlotUpdateManager;
import n1luik.K_multi_threading.core.util.concurrent.LockIdentityHashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(value = SlotUpdateManager.class)
public class SlotUpdateManagerMixin {
    @Shadow private static long serverTicks;
    @Unique
    private static volatile LockIdentityHashMap<CraftingInventoryExt, Runnable> KMT$UPDATES = new LockIdentityHashMap<>();
    //private static final VarHandle KMT$UPDATES_HANDLE;
    //static {
    //    try {
    //        KMT$UPDATES_HANDLE = MethodHandles.lookup().findVarHandle(SlotUpdateManager.class, "KMT$UPDATES", Map.class);
    //    } catch (NoSuchFieldException | IllegalAccessException e) {
    //        throw new RuntimeException(e);
    //    }
    //}

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void queueSlotUpdate(Level level, Player player, CraftingInventoryExt inv, ResultContainer result) {
        if (!level.isClientSide) {
            Runnable task = () -> FastBenchUtil.slotChangedCraftingGrid(level, player, inv, result);
            KMT$UPDATES.putIfAbsent(inv, task);
        }
    }

    @Overwrite
    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post e) {
        if (++serverTicks % (long) FastBench.gridUpdateInterval == 0L) {
            //VarHandle kmt$updatesHandle = ;
            //KMT$UPDATES_HANDLE.compareAndSet(, new LockIdentityHashMap<>());
            LockIdentityHashMap<CraftingInventoryExt, Runnable> kmt$UPDATES = KMT$UPDATES;
            KMT$UPDATES = new LockIdentityHashMap<>();
            kmt$UPDATES.values().forEach(Runnable::run);
        }

    }

    @Overwrite
    @SubscribeEvent
    public static void stop(ServerStoppedEvent e) {
        KMT$UPDATES = new LockIdentityHashMap<>();
    }


}