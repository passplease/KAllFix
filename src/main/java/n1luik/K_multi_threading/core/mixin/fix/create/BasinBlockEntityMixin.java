package n1luik.K_multi_threading.core.mixin.fix.create;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {
    @Shadow(remap = false) protected List<ItemStack> spoutputBuffer;

    @Shadow(remap = false) protected List<FluidStack> spoutputFluidBuffer;

    @Redirect(method = "read", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;spoutputBuffer:Ljava/util/List;", opcode = Opcodes.PUTFIELD, remap = false), remap = false)
    private void redirect(BasinBlockEntity instance, List<ItemStack> value) {
        spoutputBuffer = new CopyOnWriteArrayList<>(value);
    }
    @Redirect(method = "read", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;spoutputFluidBuffer:Ljava/util/List;", opcode = Opcodes.PUTFIELD, remap = false), remap = false)
    private void redirect2(BasinBlockEntity instance, List<FluidStack> value) {
        spoutputFluidBuffer = new CopyOnWriteArrayList<>(value);
    }
}