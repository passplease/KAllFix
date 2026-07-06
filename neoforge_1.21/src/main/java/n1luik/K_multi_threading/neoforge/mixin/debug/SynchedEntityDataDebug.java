package n1luik.K_multi_threading.neoforge.mixin.debug;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 某视神经没用着没有发现我写的客户端可能需要加我的调试，卵用没有
 */
@Deprecated
@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataDebug {

    @Inject(method = "defineId", at = @At("RETURN"))
    private static void defineId(Class<? extends Entity> p_135354_, EntityDataSerializer<?> p_135355_, CallbackInfoReturnable<EntityDataAccessor<?>> cir) {
        Base.LOGGER.info("defineId: {} {}", p_135354_, cir.getReturnValue().id(), new Throwable());
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
