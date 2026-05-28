package n1luik.KAllFix.mixin.mixinfix;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NodeEvaluator.class)
public class NodeEvaluatorMixin {
    @Redirect(method = "<init>", at = @At(value = "NEW", target = "()Lit/unimi/dsi/fastutil/ints/Int2ObjectOpenHashMap;"))
    private Int2ObjectOpenHashMap impl1(){
        return new Int2ObjectOpenHashMap<>(256);
    }
}