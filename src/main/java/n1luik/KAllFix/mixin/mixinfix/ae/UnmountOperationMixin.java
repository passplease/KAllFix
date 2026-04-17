package n1luik.KAllFix.mixin.mixinfix.ae;

import appeng.api.storage.MEStorage;
import n1luik.KAllFix.Imixin.mod.ae.NetworkStoragePipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "appeng.me.storage.NetworkStorage$UnmountOperation")
public abstract class UnmountOperationMixin implements NetworkStoragePipe {
    @Shadow public abstract MEStorage storage();

    @Override
    public MEStorage KAllFix$pipe() {
        return storage();
    }
}