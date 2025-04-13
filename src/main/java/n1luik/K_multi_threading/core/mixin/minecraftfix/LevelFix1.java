package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.util.concurrent.LockArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

@Mixin(Level.class)
public class LevelFix1 {
    @Shadow @Final private Thread thread;

    @Shadow public ArrayList<BlockSnapshot> capturedBlockSnapshots;
    @Unique
    protected final Lock K_multi_threading$lock_FreshBlockEntities = new java.util.concurrent.locks.ReentrantLock();

    @Redirect(method = "getBlockEntity",at = @At(value = "INVOKE",target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    public Thread fix1(){
        return thread;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fix2(WritableLevelData p_270739_, ResourceKey p_270683_, RegistryAccess p_270200_, Holder p_270240_, Supplier p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_, CallbackInfo ci){
        capturedBlockSnapshots = new LockArrayList<>();
    }

    @Inject(method = "addFreshBlockEntities",at = @At("HEAD"), remap = false)
    public void fix3(Collection<BlockEntity> beList, CallbackInfo ci){
        K_multi_threading$lock_FreshBlockEntities.lock();
    }

    @Inject(method = "addFreshBlockEntities",at = @At("RETURN"), remap = false)
    public void fix4(Collection<BlockEntity>  beList, CallbackInfo ci){
        K_multi_threading$lock_FreshBlockEntities.unlock();
    }

    @Inject(method = "tickBlockEntities",at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    public void fix5(CallbackInfo ci){
        K_multi_threading$lock_FreshBlockEntities.unlock();
    }
    @Inject(method = "tickBlockEntities",at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;isEmpty()Z", ordinal = 0))
    public void fix6(CallbackInfo ci){
        K_multi_threading$lock_FreshBlockEntities.lock();
    }

}
