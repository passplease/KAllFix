package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(DimensionDataStorage.class)
public abstract class DimensionDataStorageFix1 {
    @Shadow @Final private Map<String, SavedData> cache;

    @Shadow @Nullable protected abstract <T extends SavedData> T readSavedData(Function<CompoundTag, T> p_164869_, String p_164870_);

    @Shadow @Nullable public abstract <T extends SavedData> T get(Function<CompoundTag, T> p_164859_, String p_164860_);

    @Shadow public abstract void set(String p_164856_, SavedData p_164857_);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public <T extends SavedData> T computeIfAbsent(Function<CompoundTag, T> p_164862_, Supplier<T> p_164863_, String p_164864_) {
        T t = this.get(p_164862_, p_164864_);
        if (t != null) {
            return t;
        } else {
            synchronized (this){
                T t2 = this.get(p_164862_, p_164864_);
                if (t2 != null) {
                    return t2;
                } else {
                    T t1 = p_164863_.get();
                    this.set(p_164864_, t1);
                    return t1;
                }
            }
        }
    }
}
