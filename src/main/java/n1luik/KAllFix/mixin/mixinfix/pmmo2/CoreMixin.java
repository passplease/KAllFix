package n1luik.KAllFix.mixin.mixinfix.pmmo2;

import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.config.codecs.EnhancementsData;
import harmonised.pmmo.config.readers.CoreLoader;
import harmonised.pmmo.core.Core;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = Core.class, remap = false)
public class CoreMixin {
    @Shadow @Final private CoreLoader loader;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public Map<String, Integer> getEnchantmentReqs(ResourceLocation enchantID, int enchantLvl) {
        var orDefault = ((EnhancementsData) this.loader.getLoader(ObjectType.ENCHANTMENT).getData(enchantID)).skillArray().getOrDefault(enchantLvl, null);
        return orDefault == null ? new HashMap<>(): orDefault;
    }
}