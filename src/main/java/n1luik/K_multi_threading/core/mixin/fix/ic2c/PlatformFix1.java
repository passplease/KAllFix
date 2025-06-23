package n1luik.K_multi_threading.core.mixin.fix.ic2c;

import ic2.core.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = Platform.class, remap = false)
public class PlatformFix1 {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean isRendering() {
        return false;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean isSimulating() {
        return true;
    }
}
