package n1luik.K_multi_threading.neoforge.mixin.debug;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

/**
 * 某视神经没用着没有发现我写的客户端可能需要加我的调试，卵用没有
 */
@Deprecated
@Mixin(SynchedEntityData.Builder.class)
public abstract class SynchedEntityDataBuildDebug {

    @Shadow @Final private SynchedEntityData.DataItem<?>[] itemsById;

    @Inject(method = "build", at = @At("HEAD"))
    private  void defineId(CallbackInfoReturnable<SynchedEntityData> cir) {
        for (int i = 0; i < itemsById.length; i++) {
            SynchedEntityData.DataItem<?> dataItem = itemsById[i];
            if (dataItem != null) {
                Base.LOGGER.info("defineId: {} {}", i, dataItem.getValue());
            }else {
                Base.LOGGER.info("defineId: {} null", i);
            }
        }
        Base.LOGGER.info("defineId: {} ", Arrays.toString(itemsById), new Throwable());
    }
    ///**
    // * @author
    // * @reason
    // */
    //@Overwrite
    //public <T> void define(EntityDataAccessor<T> p_135373_, T p_135374_) {
    //    int i = p_135373_.getId();
    //    if (i > 254) {
    //        throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254) | ");
    //    } else if (this.itemsById.containsKey(i)) {
    //        throw new IllegalArgumentException("Duplicate id value for " + i + "! | " + Minecraft.getInstance().level.getEntity(i));
    //    } else if (EntityDataSerializers.getSerializedId(p_135373_.getSerializer()) < 0) {
    //        throw new IllegalArgumentException("Unregistered serializer " + p_135373_.getSerializer() + " for " + i + "!");
    //    } else {
    //        this.createDataItem(p_135373_, p_135374_);
    //    }
    //}

}
