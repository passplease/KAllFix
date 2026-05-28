package n1luik.KAllFix.mixin.mixinfix.create_connected;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.impl.shadow.S;

import java.util.function.Supplier;


@Mixin(targets = "com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlockEntity$InventoryBridgeHandler", remap = false, priority = 900)
public class InventoryBridgeHandlerMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;withInitial(Ljava/util/function/Supplier;)Ljava/lang/ThreadLocal;", remap = false), remap = false)
    private ThreadLocal impl1(Supplier<? extends S> supplier){
        return null;
    }
    @Unique
    private boolean recursionGuard_ = false;
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private <T> T preventRecursion(Supplier<T> value, T defaultValue) {
        if (recursionGuard_) {
            return defaultValue;
        } else {
            recursionGuard_ = true;
            T result = value.get();
            recursionGuard_ = false;
            return result;
        }
    }
}