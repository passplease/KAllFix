package n1luik.K_multi_threading.neoforge.mixin.fix.curios;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.DynamicStackHandler;

import javax.annotation.Nonnull;

@Mixin(value = DynamicStackHandler.class)
public abstract class DynamicStackHandlerMixin extends ItemStackHandler implements IDynamicStackHandler { // TODO 可能不行
    @Shadow protected NonNullList<ItemStack> previousStacks;
    @Unique
    public volatile int oldSize = -1;
    @Inject(method = {"grow", "shrink"}, at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        oldSize = stacks.size();
    }

    @Unique
    protected boolean asyncValidateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            if (slot >= oldSize) return false;
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
        }
        return true;
    }
    @Overwrite
    public void setPreviousStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (this.asyncValidateSlotIndex(slot)) {
            return;
        }
        this.previousStacks.set(slot, stack);
        this.onContentsChanged(slot);
    }

}