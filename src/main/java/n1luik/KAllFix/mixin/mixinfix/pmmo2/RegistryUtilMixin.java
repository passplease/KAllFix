package n1luik.KAllFix.mixin.mixinfix.pmmo2;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.config.codecs.EnhancementsData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import harmonised.pmmo.util.RegistryUtil;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = RegistryUtil.class, remap = false)
public class RegistryUtilMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static ResourceLocation getId(Item item) {
        return item.builtInRegistryHolder().key().location();//ForgeRegistries.ITEMS.getKey(item);
    }
}