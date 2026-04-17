package n1luik.KAllFix.mixin.mixinfix.ae;

import n1luik.KAllFix.Imixin.mod.ae.NetworkStoragePipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "appeng.me.storage.NetworkStorage$QueuedOperation")
public interface QueuedOperationMixin extends NetworkStoragePipe {

}