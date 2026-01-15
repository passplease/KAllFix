package n1luik.K_multi_threading.core.mixin.fix.nameless_trinkets;

import com.cozary.nameless_trinkets.utils.MaxHealthFix;
import n1luik.K_multi_threading.core.util.ListRemoveIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.impl.shadow.E;

import java.util.Iterator;
import java.util.List;

@Mixin(value = MaxHealthFix.class, remap = false)
public class MaxHealthFixMixin {
    @Redirect(method = "onWorldTick", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    public Iterator<E> fix1(List instance){
        return new ListRemoveIterator<>(instance, instance.iterator());
    }

}