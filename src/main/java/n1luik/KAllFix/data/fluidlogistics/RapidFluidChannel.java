package n1luik.KAllFix.data.fluidlogistics;

import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static com.yision.fluidlogistics.item.CompressedTankItem.ensureIdentity;

public class RapidFluidChannel {
    //动态修复现有包裹的ntb但是有额外开销
    private final static boolean DynamicUp = !Boolean.getBoolean("KAF-fluidlogistics.DisabledDynamicUp");
    //如果新增流体会导致表变化所以需要一个校验哈希
    private static final Object2IntAVLTreeMap<Fluid> IdLGet = new Object2IntAVLTreeMap<>((a, b)->{//进行排序让重启也可以使用老的缓存
        return Integer.compareUnsigned(
                a.builtInRegistryHolder().key().location().hashCode(),
                b.builtInRegistryHolder().key().location().hashCode()
        );
    });
    private static final Fluid[] IdList;
    private static final int[] IdHashList;
    private static final int IdMax;
    static {
        //List<FluidState> list = new ArrayList<>();
        ArrayList<Fluid> fluids = new ArrayList<>(BuiltInRegistries.FLUID.stream().toList());
        fluids.sort((a, b)->{//进行排序让重启也可以使用老的缓存
            return Integer.compareUnsigned(
                    a.builtInRegistryHolder().key().location().hashCode(),
                    b.builtInRegistryHolder().key().location().hashCode()
            );
        });
        //for (Fluid fluid : fluids) {
        //    list.addAll(fluid.getStateDefinition().getPossibleStates());
        //}
        IdList = fluids.toArray(Fluid[]::new);
        IdHashList = new int[IdList.length];
        int p=0;
        var nbt = new CompoundTag();
        for (var fluidStack : IdList) {
            IdHashList[p] = fluidStack.builtInRegistryHolder().key().location().hashCode();//.getValues().hashCode();
            IdLGet.put(fluidStack, p++);
        }
        IdLGet.defaultReturnValue(-1);
        IdMax = IdList.length;

    }

    public static void setFluid(ItemStack stack, FluidStack fluid, boolean Virtual){
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("Fluid", fluid.writeToNBT(new CompoundTag()));
        tag.putBoolean("Virtual", Virtual);
        ensureIdentity(stack);
        if (fluid.hasTag())return;
        int id = IdLGet.getInt(fluid.getFluid());
        if (id >= 0) {
            tag.putInt("FluidHash", IdHashList[id]);
            tag.putInt("FluidId", id);
            tag.putInt("FluidSize", fluid.getAmount());
        }

    }
    public static void fixFluidStack(ItemStack stack,FluidStack fluid){
        CompoundTag tag = stack.getTag();

        int id = IdLGet.getInt(fluid.getFluid());
        if (id >= 0) {
            tag.putInt("FluidHash", IdHashList[id]);
            tag.putInt("FluidId", id);
            tag.putInt("FluidSize", fluid.getAmount());
        }
    }
    public static FluidStack getFluidStack(ItemStack stack){

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Fluid"))return FluidStack.EMPTY;
        if (!tag.contains("FluidId")) {
            if(!DynamicUp || tag.contains("Tag"))return FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));
            var fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));

            fixFluidStack(stack, fluid);
            return fluid;
        }
        int fluidId = tag.getInt("FluidId");
        if (fluidId < IdMax){
            if (IdHashList[fluidId] == tag.getInt("FluidHash")){
                return new FluidStack(IdList[fluidId], tag.getInt("FluidSize"));
            }
        }
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));
        fixFluidStack(stack, fluid);
        return fluid;
    }
}
