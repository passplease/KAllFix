package n1luik.KAllFix.fix.farm_and_charm;

import n1luik.KAllFix.forge.ModInit;
import net.minecraft.resources.ResourceLocation;

public class FACOptimizeTag {
    public static final int ChickenCoopBlockTag;
    static{
        ChickenCoopBlockTag = ModInit.OPTIMIZE_TAG_MANAGER.create(new ResourceLocation("chunk")).getIndex(new ResourceLocation("farm_and_charm", "chicken_coop_block"));
    }
}
