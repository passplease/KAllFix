package n1luik.KAllFix.forge.RenderOptimizer.mixin;

import n1luik.KAllFix.forge.RenderOptimizer.Config;
import n1luik.KAllFix.forge.RenderOptimizer.Imixin.IEntityOptimizerData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements IEntityOptimizerData {
    @Unique
    private int KAllFix$tick = 0;
    @Unique
    private boolean KAllFix$isOptimizer = false;

    @Override
    public int KAllFix$getTick() {
        return KAllFix$tick++;
    }

    @Override
    public void KAllFix$setIsOptimizer(boolean isOptimizer) {
        KAllFix$isOptimizer = isOptimizer;
    }

    @Override
    public boolean KAllFix$isOptimizer() {
        return KAllFix$isOptimizer && Config.Enable;
    }
}
