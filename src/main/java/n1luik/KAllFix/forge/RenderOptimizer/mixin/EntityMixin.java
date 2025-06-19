package n1luik.KAllFix.forge.RenderOptimizer.mixin;

import n1luik.KAllFix.forge.RenderOptimizer.Imixin.IEntityOptimizerData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements IEntityOptimizerData {
    private int KAllFix$tick = 0;

    @Override
    public int KAllFix$getTick() {
        return KAllFix$tick++;
    }
}
