package n1luik.KAllFix.mixin;

import n1luik.KAllFix.Imixin.IEnchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Enchantment.class)
public class EnchantmentMixin implements IEnchantment {
    @Unique
    private ResourceLocation KAllFix$idBuf;

    @Override
    public ResourceLocation KAllFix$getId() {
        return KAllFix$idBuf;
    }

    @Override
    public void KAllFix$setId(ResourceLocation id) {
        KAllFix$idBuf = id;
    }
}