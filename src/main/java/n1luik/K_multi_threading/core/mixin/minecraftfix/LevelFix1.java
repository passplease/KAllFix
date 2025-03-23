package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.util.concurrent.LockArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

@Mixin(Level.class)
public class LevelFix1 {
    @Shadow @Final private Thread thread;

    @Shadow public ArrayList<BlockSnapshot> capturedBlockSnapshots;

    @Redirect(method = "getBlockEntity",at = @At(value = "INVOKE",target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    public Thread fix1(){
        return thread;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fix2(WritableLevelData p_270739_, ResourceKey p_270683_, RegistryAccess p_270200_, Holder p_270240_, Supplier p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_, CallbackInfo ci){
        capturedBlockSnapshots = new LockArrayList<>();
    }
}
