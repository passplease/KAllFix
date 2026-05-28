package n1luik.KAllFix.mixin;

import n1luik.KAllFix.Imixin.IEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(value = EnchantmentHelper.class, priority = 800)
public class EnchantmentHelperMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite()
    @Nullable
    public static ResourceLocation getEnchantmentId(Enchantment p_182433_) {
        IEnchantment iEnchantment = (IEnchantment) p_182433_;
        var ret = iEnchantment.KAllFix$getId();
        if (ret != null) return ret;
        ResourceLocation key = BuiltInRegistries.ENCHANTMENT.getKey(p_182433_);
        iEnchantment.KAllFix$setId(key);
        return key;
    }
}