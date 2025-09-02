package n1luik.KAllFix.mixin.mixinfix.farm_and_charm;

import n1luik.KAllFix.Imixin.IKAFSaturationTracker;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = SaturationTracker.class, remap = false)
public class SaturationTrackerMixin implements IKAFSaturationTracker {
    @Shadow(remap = false) private int foodCounter;
    @Unique
    private int KAllFix$foodCounterOld = Integer.MAX_VALUE;

    @Override
    public boolean KAllFix$testSendData() {
        if (foodCounter != KAllFix$foodCounterOld) {
            KAllFix$foodCounterOld = foodCounter;
            return false;
        }
        return true;
    }

    @Override
    public boolean KAllFix$testSendData(int foodCounter) {
        if (foodCounter != KAllFix$foodCounterOld) {
            KAllFix$foodCounterOld = foodCounter;
            return false;
        }
        return true;
    }
}
