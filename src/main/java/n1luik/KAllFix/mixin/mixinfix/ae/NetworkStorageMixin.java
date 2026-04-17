package n1luik.KAllFix.mixin.mixinfix.ae;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.me.storage.NetworkStorage;
import n1luik.KAllFix.Imixin.mod.ae.NetworkStoragePipe;
import n1luik.KAllFix.data.ae.PreferredStorageForBuf;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = NetworkStorage.class, priority = 800, remap = false)
public abstract class NetworkStorageMixin {
    @Unique
    private NavigableMap<Integer, List<MEStorage>> descendingCache; // 【新增】缓存
    @Unique
    private final AtomicInteger descendingCacheLock = new AtomicInteger(0);
    @Shadow(remap = false) @Final private NavigableMap<Integer, List<MEStorage>> priorityInventory;

    @Shadow(remap = false) private boolean mountsInUse;

    @Shadow(remap = false) protected abstract boolean isQueuedForRemoval(MEStorage inv);

    @Shadow(remap = false) protected abstract void flushQueuedOperations();

    //@Unique
    //private volatile PreferredStorageForBuf preferredStorageForBuf = null;
    //public boolean isPreferredStorageFor(AEKey input, IActionSource source) {
    //    if (preferredStorageForBuf != null) {
    //        if (filter.isWhitelisted(input)) {
    //            if (input == lastCheckedKey && System.currentTimeMillis() - lastCheckTime < CACHE_TTL_MS) {
    //                return lastCheckResult;
    //            }
    //            if (extract(input, 1, Actionable.SIMULATE, source) > 0) {
    //                preferredStorageForBuf = input;
    //                lastCheckResult = true;
    //                lastCheckTime = System.currentTimeMillis();
    //                return true;
    //            }
    //        }
    //    }
    //}
    @Shadow(remap = false) private @Nullable List<NetworkStoragePipe> queuedOperations;
    @Unique
    private final Map<MEStorage, Integer> storageToPriority = new ConcurrentHashMap<>(); // 【新增】
    @Inject(method = "mount", at = @At(value = "INVOKE", target = "Ljava/util/NavigableMap;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;", remap = false), remap = false)
    private void mount(int priority, MEStorage inventory, CallbackInfo ci){
        storageToPriority.put(inventory, priority);
        while (!descendingCacheLock.compareAndSet(0, 1));
        descendingCache = null;
        descendingCacheLock.set(0);
    }
    @Inject(method = "unmount", at = @At(value = "INVOKE", target = "Ljava/util/NavigableMap;entrySet()Ljava/util/Set;", remap = false), remap = false, cancellable = true)
    private void unmount(MEStorage inventory, CallbackInfo ci){
        ci.cancel();
        var priority = storageToPriority.remove(inventory);
        if (priority != null) {
            var inventories = priorityInventory.get(priority);
            if (inventories != null && inventories.remove(inventory)) {
                if (inventories.isEmpty()) {
                    priorityInventory.remove(priority);
                }
            }
        }
    }
    @Unique
    private NavigableMap<Integer, List<MEStorage>> getDescendingInventory() {
        if (descendingCache == null) {
            while (!descendingCacheLock.compareAndSet(0, 1));
            descendingCache = priorityInventory.descendingMap();
            descendingCacheLock.set(0);

        }
        return descendingCache;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (this.mountsInUse) {
            return 0L;
        } else {
            long extracted = 0L;
            this.mountsInUse = true;

            try {
                var removalSet = new HashSet<MEStorage>();
                if (queuedOperations != null) {
                    for (NetworkStoragePipe queuedOperation : queuedOperations) {
                        MEStorage meStorage;
                        if (queuedOperation != null && (meStorage = queuedOperation.KAllFix$pipe()) != null)
                            removalSet.add(meStorage);
                    }

                }
                for(List<MEStorage> invList : getDescendingInventory().values()) {
                    Iterator<MEStorage> ii = invList.iterator();

                    while(ii.hasNext() && extracted < amount) {
                        MEStorage inv = (MEStorage)ii.next();
                        //if (!this.isQueuedForRemoval(inv)) {
                        if (!removalSet.contains(inv)) {
                            extracted += inv.extract(what, amount - extracted, mode, source);
                        }
                    }
                }
            } finally {
                this.mountsInUse = false;
            }

            this.flushQueuedOperations();
            return extracted;
        }
    }
}