package n1luik.K_multi_threading.core.mixin.fix.create;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BrassTunnelBlockEntity.class, remap = false)
public class BrassTunnelBlockEntityFix1 {
    @Redirect(method = "insertIntoTunnel", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/belt/behaviour/DirectBeltInputBehaviour;handleInsertion(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/item/ItemStack;", remap = false), remap = false)
    private ItemStack handleInsertion(DirectBeltInputBehaviour instance, ItemStack stack, Direction side, boolean simulate) {
        ItemStack itemStack = instance.handleInsertion(stack, side, simulate);
        return itemStack == null ? ItemStack.EMPTY : itemStack;
    }
}