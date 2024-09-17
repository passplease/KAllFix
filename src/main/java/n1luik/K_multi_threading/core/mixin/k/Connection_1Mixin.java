package n1luik.K_multi_threading.core.mixin.k;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net/minecraft/network/Connection$1")
public class Connection_1Mixin {
    @ModifyConstant(method = "initChannel", constant = @Constant(intValue = 30))
    public int fix1(int constant){
        return 500;
    }
}
