package n1luik.KAllFix.mixin.mixinfix;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(value = ItemStack.class, priority = 800)
public class ItemStackMixin {

    @Redirect(method = "isSameItemSameTags", at = @At(value = "INVOKE", remap = false, target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    private static boolean o1(Object a, Object b) {
        return a == b ? true : (a != null && b != null && (a.hashCode() == b.hashCode() && a.equals(b)));
    }
}