package n1luik.K_multi_threading.core.mixin.fix.ae2;

import appeng.me.cluster.IAECluster;
import appeng.me.cluster.MBCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;

@Mixin(value = MBCalculator.class, remap = false)
public class MBCalculatorFix1 {
    @Unique
    private static final ThreadLocal<WeakReference<IAECluster>> modificationInProgress = ThreadLocal.withInitial(() -> new WeakReference((Object) null));

    @Overwrite(remap = false)
    public static boolean isModificationInProgress() {
        return modificationInProgress.get() != null;
    }

    @Overwrite(remap = false)
    public static void setModificationInProgress(IAECluster cluster) {
        IAECluster inProgress = (IAECluster)modificationInProgress.get();
        if (inProgress != cluster) {
            if (inProgress != null && cluster != null) {
                throw new IllegalStateException("A modification is already in-progress for: " + String.valueOf(inProgress));
            } else {
                modificationInProgress.set(new WeakReference(cluster));
            }
        }
    }

}