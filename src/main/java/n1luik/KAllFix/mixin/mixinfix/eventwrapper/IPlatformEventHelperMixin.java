package n1luik.KAllFix.mixin.mixinfix.eventwrapper;

import io.github.lounode.eventwrapper.eventbus.api.IPlatformEventHelper;
import n1luik.KAllFix.fix.eventwrapper.VarHandleFieldSync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = IPlatformEventHelper.class)
public interface IPlatformEventHelperMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    static void syncEventData(Object from, Object to) {
        VarHandleFieldSync.syncEventData(from, to);
    }
}