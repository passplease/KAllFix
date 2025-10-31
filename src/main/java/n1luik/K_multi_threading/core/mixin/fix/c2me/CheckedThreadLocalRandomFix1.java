package n1luik.K_multi_threading.core.mixin.fix.c2me;

import com.ishland.c2me.fixes.worldgen.threading_issues.common.CheckedThreadLocalRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CheckedThreadLocalRandom.class, remap = false)
public class CheckedThreadLocalRandomFix1 {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private boolean isSafe() {
        return true;
    }
}
