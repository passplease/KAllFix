package n1luik.K_multi_threading.neoforge.mixin.fix.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

@Mixin(value = ChainConveyorBlockEntity.class)
public class ChainConveyorBlockEntityMixin {
    @WrapOperation(method = "read", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/nbt/NBTHelper;readCompoundList(Lnet/minecraft/nbt/ListTag;Ljava/util/function/Function;)Ljava/util/List;"))
    private static <T> List<T> readCompoundList(ListTag listNBT, Function<CompoundTag, T> deserializer, Operation<List<T>> original) {
        return new CopyOnWriteArrayList<>(original.call(listNBT, deserializer));
    }
}