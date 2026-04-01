package n1luik.KAllFix.mixin.mixinfix.fluidlogistics;

import com.yision.fluidlogistics.item.CompressedTankItem;
import n1luik.KAllFix.data.fluidlogistics.RapidFluidChannel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CompressedTankItem.class, remap = false)
public class CompressedTankItemMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static FluidStack getFluid(ItemStack stack) {
        return RapidFluidChannel.getFluidStack(stack);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static void setFluid(ItemStack stack, FluidStack fluid) {
        RapidFluidChannel.setFluid(stack, fluid, false);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static void setFluidVirtual(ItemStack stack, FluidStack fluid) {
        RapidFluidChannel.setFluid(stack, fluid, true);
    }
}