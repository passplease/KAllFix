package n1luik.K_multi_threading.core.mixin.fix.create;

import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import n1luik.K_multi_threading.core.util.ListRemoveIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;

@Mixin(value = BeltInventory.class, remap = false)
public class BeltInventoryFix1 {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), remap = false)
    private <T> Iterator<T> fix1(List<T> list) {
        return new ListRemoveIterator<>(list, list.iterator());
    }
}
